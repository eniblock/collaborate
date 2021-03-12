# Collaborate

## Catalog

```shell script
k3d cluster delete collaborate-catalog
```

```shell script
helm registry login registry.gitlab.com
```

```shell script
k3d registry create registry.localhost -p 5000
```

```shell script
bash build.sh
```

```shell script
k3d cluster create collaborate-catalog --port 80:80@loadbalancer --port 443:443@loadbalancer --registry-use k3d-registry.localhost:5000
```

```shell script
helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --version v1.2.0 \
  --create-namespace \
  --set installCRDs=true
```

```shell script
helm registry login registry.gitlab.com
```

```shell script
export HELM_EXPERIMENTAL_OCI=1
```

```shell script
helm install collaborate-catalog ./helm/collaborate-catalog --values ./helm/collaborate-catalog/values-dev.yaml
```