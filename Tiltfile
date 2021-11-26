#!/usr/bin/env python

config.define_bool("no-volumes")
cfg = config.parse()

clk_k8s = 'clk -a --force-color k8s -c ' + k8s_context() + ' '

load('ext://kubectl_build', 'image_build', 'kubectl_build_registry_secret',
     'kubectl_build_enable')
kubectl_build_enable(
    local(clk_k8s + 'features --field value --format plain kubectl_build'))

if config.tilt_subcommand == 'up':
  # declare the host we'll be using locally in k8s dns
  local(clk_k8s + 'add-domain col.localhost')
  # update the helm package dependencies a first time at startup, so helm can load the helm chart
  local(clk_k8s + 'helm-dependency-update helm/collaborate-dapp')

# manually download the dependencies
local_resource('helm dependencies',
               clk_k8s + 'helm-dependency-update helm/collaborate-dapp -ft Tiltfile',
               trigger_mode=TRIGGER_MODE_MANUAL, auto_init=False)

overridedValues = [
  'api.traefik.pilot.token=' + os.getenv('TRAEFIK_PILOT_TOKEN', '')
]
if os.getenv('BUSINESS_DATA_SC', '') != '':
  overridedValues \
    .append('api.businessDataContractAddress=' + os.getenv('BUSINESS_DATA_SC'))
if os.getenv('DIGITAL_PASSPORT_SC', '') != '':
  overridedValues \
    .append(
    'api.digitalPassportContractAddress=' + os.getenv('DIGITAL_PASSPORT_SC'))

k8s_yaml(
    helm(
        'helm/collaborate-dapp',
        values=['./helm/collaborate-dapp/values-dev.yaml'],
        name='col',
        set=overridedValues
    )
)

image_build(
    'registry.gitlab.com/xdev-tech/xdev-enterprise-business-network/collaborate/dapp/api',
    'dapp/api',
    target='dev'
)

image_build(
    'registry.gitlab.com/xdev-tech/xdev-enterprise-business-network/collaborate/dapp/iam',
    'dapp/iam'
)

print('exposing maildev on port 1080')
k8s_resource('col-collaborate-dapp-maildev',
             port_forwards=['1080:80', '1025:25'])

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
  'col-collaborate-dapp-ipfs',
]:
  k8s_resource(r, labels=['collaborate'])
for r in [
  'col-keycloak-db',
  'col-keycloak',
]:
  k8s_resource(r, labels=['keycloak'])

print('exposing tag-api on port 3333')
k8s_resource('col-tag-api', port_forwards='3333:3333',
             resource_deps=['col-tag-rabbitmq'])
k8s_resource('col-tag-send-transactions-worker',
             resource_deps=['col-tag-rabbitmq'])
k8s_resource('col-tag-injection-worker', resource_deps=['col-tag-rabbitmq'])
k8s_resource('col-tag-operation-status-worker',
             resource_deps=['col-tag-rabbitmq'])
k8s_resource('col-collaborate-dapp-api',
             resource_deps=['col-tag-api', 'col-keycloak',
                            'col-collaborate-dapp-ipfs'],
             port_forwards=['5001:5000'])
k8s_resource('col-api-db',
             port_forwards=['5432:5432'])
k8s_resource('col-tag-vault', port_forwards=['8270:8200'])

print('exposing IPFS API on port 5010')
k8s_resource('col-collaborate-dapp-ipfs', port_forwards=['5010:5001'])
k8s_resource('col-tag-rabbitmq', port_forwards=['15672:15672'])

local_resource('helm lint',
               'docker run --rm -t -v $PWD:/app registry.gitlab.com/xdev-tech/build/helm:2.0' +
               ' lint helm/collaborate-dapp --values helm/collaborate-dapp/values-dev.yaml',
               'helm/collaborate-dapp/', allow_parallel=True)

if config.tilt_subcommand == 'down' and not cfg.get("no-volumes"):
  local(
      'kubectl --context ' + k8s_context()
      + ' delete pvc --selector=app.kubernetes.io/instance=col --wait=false'
  )
