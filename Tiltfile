#!/usr/bin/env python

# load('./kubectl_build.ext', 'kubectl_build')
# os.environ['KUBECTL_BUILD_REGISTRY_SECRET'] = 'gitlab-registry'

config.define_bool("no-volumes")
cfg = config.parse()

clk_k8s = 'clk -a --force-color k8s -c ' + k8s_context() + ' '
if config.tilt_subcommand == 'up':
    # check that registry gitlab secrets are properly configured and login with helm
    docker_config = decode_json(local(clk_k8s + 'docker-credentials -hd gitlab-registry', quiet=True))
    os.environ['CI_JOB_TOKEN'] = docker_config['registry.gitlab.com']['password']
    # declare the host we'll be using locally in k8s dns
    local(clk_k8s + 'add-domain col.localhost')
    # update the helm package dependencies a first time at startup, so helm can load the helm chart
    local(clk_k8s + 'helm-dependency-update helm/collaborate-dapp')

# manually download the dependencies
local_resource('helm dependencies',
               clk_k8s + 'helm-dependency-update helm/collaborate-dapp -ft Tiltfile',
               trigger_mode=TRIGGER_MODE_MANUAL, auto_init=False)

k8s_yaml(
    helm(
        'helm/collaborate-dapp',
        values=['./helm/collaborate-dapp/values-dev.yaml'],
        name='col',
    )
)

docker_build(
    'registry.gitlab.com/the-blockchain-xdev/xdev-product/collaborate/dapp/api',
    'dapp/api',
    target='dev'
)

docker_build(
    'registry.gitlab.com/the-blockchain-xdev/xdev-product/collaborate/dapp/iam',
    'dapp/iam'
)

print('exposing maildev on port 1080')
k8s_resource('col-collaborate-dapp-maildev', port_forwards=['1080:80', '1025:25'])

# group the resources in tilt webui
for r in [
    'col-tag-api',
    'col-tag-injection-worker',
    'col-tag-operation-status-worker',
    'col-tag-send-transactions-worker',
    'col-tag-db',
    'col-tag-rabbitmq',
    'col-tag-vault',
]:
    k8s_resource(r, labels=['tag'])
for r in [
    'col-collaborate-dapp-api',
    'col-collaborate-dapp-maildev',
    'col-api-db',
]:
    k8s_resource(r, labels=['collaborate'])
for r in [
    'col-postgresql',
    'col-keycloak',
]:
    k8s_resource(r, labels=['keycloak'])

# add some dependencies, so all the resources won't start in parallel
k8s_resource('col-tag-api', resource_deps=['col-tag-rabbitmq'])
k8s_resource('col-tag-send-transactions-worker', resource_deps=['col-tag-rabbitmq'])
k8s_resource('col-tag-injection-worker', resource_deps=['col-tag-rabbitmq'])
k8s_resource('col-tag-operation-status-worker', resource_deps=['col-tag-rabbitmq'])
k8s_resource('col-collaborate-dapp-api',
             resource_deps=['col-tag-api', 'col-keycloak'],
             port_forwards=['5001:5000'])
k8s_resource('col-api-db',
             port_forwards=['5432:5432'])

local_resource('helm lint',
               'docker run --rm -t -v $PWD:/app registry.gitlab.com/the-blockchain-xdev/xdev-product/build-images/helm:1.3.0' +
               ' lint helm/collaborate-dapp --values helm/collaborate-dapp/values-dev.yaml',
               'helm/collaborate-dapp/')

if config.tilt_subcommand == 'down' and not cfg.get("no-volumes"):
  local('kubectl --context ' + k8s_context() + ' delete pvc --selector=app.kubernetes.io/instance=col --wait=false')
