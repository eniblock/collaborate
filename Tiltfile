k8s_yaml(
    helm(
        'helm/tezos-api-gateway',
        values=['./helm/tezos-api-gateway/values-dev.yaml'],
        name="tezos-api-gateway",
    )
)

k8s_yaml(
    helm(
        'helm/collaborate-catalog',
        values=['./helm/collaborate-catalog/values-dev.yaml'],
        name="collaborate-catalog",
    )
)

k8s_yaml(
    helm(
        'helm/collaborate-dapp',
        values=['./helm/collaborate-dapp/values-dev.yaml'],
        name="collaborate-dapp",
    )
)

k8s_yaml(
    helm(
        'helm/fake-datasource',
        values=['./helm/fake-datasource/values-dev.yaml'],
        name="fake-datasource",
    )
)

docker_build(
    'registry.gitlab.com/the-blockchain-xdev/xdev-product/collaborate/catalog/api',
    'catalog/api'
)

docker_build(
    'registry.gitlab.com/the-blockchain-xdev/xdev-product/collaborate/catalog/iam',
    'catalog/iam'
)

docker_build(
    'registry.gitlab.com/the-blockchain-xdev/xdev-product/collaborate/dapp/api',
    'dapp/api'
)

docker_build(
    'registry.gitlab.com/the-blockchain-xdev/xdev-product/collaborate/dapp/iam',
    'dapp/iam'
)

docker_build(
    'registry.gitlab.com/the-blockchain-xdev/xdev-product/collaborate/fake-datasource/datasource-api',
    'fake-datasource/datasource-api'
)

docker_build(
    'registry.gitlab.com/the-blockchain-xdev/xdev-product/collaborate/fake-datasource/datasource-iam',
    'fake-datasource/datasource-iam'
)