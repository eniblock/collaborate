# Collaborate

## Requirements

- [Docker](https://docs.docker.com/engine/install/#server)
- [k3d](https://k3d.io/#installation)
- [Helm](https://helm.sh/docs/intro/install/)
- [Tilt](https://docs.tilt.dev/install.html#linux)

## Installation

### Using the shell script

This script will delete your whole kubernetes cluster & recreate it from scratch.
Replace the placeholders with your gitlab username & personal token
```shell script
GITLAB_USER=yourgitlabusername GITLAB_PASSWORD=yourgitlabpersonaltoken ./reset.sh
```

### Or step by step

Create a local registry
```shell script
k3d registry create registry.localhost -p 5000
```

Start a k3d cluster
```shell script
k3d cluster create dev --port 80:80@loadbalancer --port 443:443@loadbalancer --port 5672:5672@loadbalancer --registry-use k3d-registry.localhost:5000
```

Install the cert manager & add an issuer
```shell script
helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --version v1.2.0 \
  --create-namespace \
  --set installCRDs=true \
  --set ingressShim.defaultIssuerName=local \
  --set ingressShim.defaultIssuerKind=ClusterIssuer \
  --wait
kubectl apply -n cert-manager -f ./helm/local.yaml
```

Login to gitlab registry. Replace the placeholders with your gitlab username & personal token
```shell script
kubectl create secret docker-registry gitlab-registry --docker-server=registry.gitlab.com --docker-username=DOCKER_USER --docker-password=DOCKER_PASSWORD --docker-email=DOCKER_EMAIL
```

Enable Docker BuildKit & Helm charts OCI
```shell script
export DOCKER_BUILDKIT=1
export HELM_EXPERIMENTAL_OCI=1
```

Add custom domains DNS
```shell script
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+tezos-api-gateway\.localhost$/!p' -e '$a172.17.0.1 tezos-api-gateway.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+catalog\.collaborate\.localhost$/!p' -e '$a172.17.0.1 catalog.collaborate.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+dapp\.collaborate\.localhost$/!p' -e '$a172.17.0.1 dapp.collaborate.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+psa\.pcc\.localhost$/!p' -e '$a172.17.0.1 psa.pcc.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+fake-datasource\.localhost$/!p' -e '$a172.17.0.1 fake-datasource.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+peugeot\.fake-datasource\.localhost$/!p' -e '$a172.17.0.1 peugeot.fake-datasource.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+citroen\.fake-datasource\.localhost$/!p' -e '$a172.17.0.1 citroen.fake-datasource.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+mobivia\.fake-datasource\.localhost$/!p' -e '$a172.17.0.1 mobivia.fake-datasource.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
```

Update the helm repositories
```shell script
helm dependency update ./helm/collaborate-catalog
helm dependency update ./helm/collaborate-dapp
helm dependency update ./helm/fake-datasource
helm dependency update ./helm/tezos-api-gateway
```

Skip TLS verification inside the network 
```shell script
traefik=$(kubectl get cm traefik -n kube-system --template='{{index .data "traefik.toml"}}' | sed -n -E -e '/insecureSkipVerify = true$/!p' -e '1i\insecureSkipVerify = true' | tr '\n' '^' | sed -E 's%"%\\"%g' | busybox xargs -0 printf '{"data": {"traefik.toml":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm traefik -n kube-system -p="$traefik"
kubectl delete pod -l app=traefik -n kube-system --wait=false
```

Destroy your k3d cluster and remove your volumes
```shell script
k3d cluster delete dev
k3d registry delete k3d.registry.localhost
docker volume prune -f
```

## Start the application

```shell script
tilt up
```

## Stop the application

```shell script
tilt down
```

# Debugging
## Java API
To enable _remote_ debug on Java dockerized application:
* `"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5000"` argument has to be given to the JVM in charge of executing the Java application in the container.
  _Where `5000` is the debug port_
* The `Tiltfile` has to bind the container debug port to a local port (see: [Tilt API doc](https://docs.tilt.dev/api.html)), for an example:
```python
k8s_resource(
    'collaborate-dapp-api',
    port_forwards=['5001:5000']
)
```
* In your IDE configure a _remote JVM debug_ configuration, for an example in IntelliJ:
  * Edit Configurations... / Remote JVM Debug
  * Specify the command line: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001`
  * OK
  * Then, select the created run configuration and click on the Debug button  
    