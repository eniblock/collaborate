#!/usr/bin/env sh

k3d cluster delete dev
k3d registry delete k3d.registry.localhost
docker volume prune -f
k3d registry create registry.localhost -p 5000
k3d cluster create dev --port 80:80@loadbalancer --port 443:443@loadbalancer --port 5672:5672@loadbalancer --registry-use k3d-registry.localhost:5000

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

kubectl create secret docker-registry gitlab-registry --docker-server=registry.gitlab.com --docker-username=${GITLAB_USER} --docker-password=${GITLAB_PASSWORD}

export DOCKER_BUILDKIT=1
export HELM_EXPERIMENTAL_OCI=1

hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+tezos-api-gateway\.localhost$/!p' -e '$a172.17.0.1 tezos-api-gateway.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+catalog\.collaborate\.localhost$/!p' -e '$a172.17.0.1 catalog.collaborate.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+dapp\.collaborate\.localhost$/!p' -e '$a172.17.0.1 dapp.collaborate.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+psa\.pcc\.localhost$/!p' -e '$a172.17.0.1 psa.pcc.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+fake-datasource\.localhost$/!p' -e '$a172.17.0.1 fake-datasource.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+peugeot\.fake-datasource\.localhost$/!p' -e '$a172.17.0.1 peugeot.fake-datasource.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+citroen\.fake-datasource\.localhost$/!p' -e '$a172.17.0.1 citroen.fake-datasource.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"
hosts=$(kubectl get cm coredns -n kube-system --template='{{.data.NodeHosts}}' | sed -n -E -e '/[0-9\.]{4,12}\s+mobivia\.fake-datasource\.localhost$/!p' -e '$a172.17.0.1 mobivia.fake-datasource.localhost' | tr '\n' '^' | busybox xargs -0 printf '{"data": {"NodeHosts":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm coredns -n kube-system -p="$hosts"

helm dependency update ./helm/collaborate-catalog
helm dependency update ./helm/collaborate-dapp
helm dependency update ./helm/fake-datasource
helm dependency update ./helm/tezos-api-gateway

until kubectl get cm traefik -n kube-system
do
  sleep 5
done

traefik=$(kubectl get cm traefik -n kube-system --template='{{index .data "traefik.toml"}}' | sed -n -E -e '/insecureSkipVerify = true$/!p' -e '1i\insecureSkipVerify = true' | tr '\n' '^' | sed -E 's%"%\\"%g' | busybox xargs -0 printf '{"data": {"traefik.toml":"%s"}}'| sed -E 's%\^%\\n%g') && kubectl patch cm traefik -n kube-system -p="$traefik"

sleep 5

kubectl delete pod -l app=traefik -n kube-system --wait=false