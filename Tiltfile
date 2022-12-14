#!/usr/bin/env python

config.define_bool("no-volumes")
cfg = config.parse()

clk_k8s = 'clk -a --force-color k8s -c ' + k8s_context() + ' '

if config.tilt_subcommand == 'up':
  # declare the host we'll be using locally in k8s dns
  local(clk_k8s + 'add-domain col.localhost')
  # update the helm package dependencies a first time at startup, so helm can load the helm chart
  local(clk_k8s + 'helm dependency-update helm/collaborate')

# manually download the dependencies
local_resource('helm dependencies',
               clk_k8s + 'helm dependency-update helm/collaborate -ft Tiltfile',
               trigger_mode=TRIGGER_MODE_MANUAL, auto_init=False)

overridedValues = [
  'api.traefik.pilot.token=' + os.getenv('TRAEFIK_PILOT_TOKEN', '')
]

helm_values = ['./helm/collaborate/values-dev.yaml']
user_helm_values = './helm/collaborate/values-dev-user.yaml'
helm_values.extend([user_helm_values] if os.path.exists(user_helm_values) else [])
k8s_yaml(
    helm(
        'helm/collaborate',
        values=helm_values,
        name='col',
        set=overridedValues
    )
)

custom_build(
    "eniblock/collaborate-api",
    "earthly ./dapp/api+docker --ref=$EXPECTED_REF",
    ["./dapp/api"],
)

custom_build(
    "eniblock/collaborate-keycloak",
    "earthly ./dapp/iam+docker --ref=$EXPECTED_REF",
    ["./dapp/iam"],
)

print('exposing maildev on port 1080')
k8s_resource('col-collaborate-maildev',
             port_forwards=['1080:1080', '1025:1025'])

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
  'col-collaborate-api',
  'col-collaborate-maildev',
  'col-api-db',
  'col-collaborate-ipfs',
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
k8s_resource('col-collaborate-api',
             resource_deps=['col-tag-api', 'col-keycloak',
                            'col-collaborate-ipfs'],
             port_forwards=['5001:5000'])
k8s_resource('col-api-db',
             port_forwards=['5432:5432'])
k8s_resource('col-tag-vault', port_forwards=['8270:8200'])

print('exposing IPFS API on port 5010')
k8s_resource('col-collaborate-ipfs', port_forwards=['5010:5001'])
k8s_resource('col-tag-rabbitmq', port_forwards=['15672:15672'])

local_resource('helm lint',
               'earthly ./helm+lint',
               'helm/collaborate/', allow_parallel=True)

if config.tilt_subcommand == 'down' and not cfg.get("no-volumes"):
  local(
      'kubectl --context ' + k8s_context()
      + ' delete pvc --selector=app.kubernetes.io/instance=col --wait=false'
  )
