#!/usr/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $DIR
docker build -t k3d-registry.localhost:5000/collaborate/catalog/api:0.1.0 catalog/api
docker push k3d-registry.localhost:5000/collaborate/catalog/api:0.1.0

docker build -t k3d-registry.localhost:5000/collaborate/catalog/iam:0.1.0 catalog/iam
docker push k3d-registry.localhost:5000/collaborate/catalog/iam:0.1.0

docker build -t k3d-registry.localhost:5000/collaborate/dapp/api:0.1.0 dapp/api
docker push k3d-registry.localhost:5000/collaborate/dapp/api:0.1.0

docker build -t k3d-registry.localhost:5000/collaborate/dapp/iam:0.1.0 dapp/iam
docker push k3d-registry.localhost:5000/collaborate/dapp/iam:0.1.0