VERSION 0.6


test:
    BUILD ./dapp/api+test
    BUILD ./dapp/iam+test
    BUILD ./helm+lint

docker:
    ARG tag
    BUILD ./dapp/api+docker --tag=${tag}
    BUILD ./dapp/iam+docker --tag=${tag}
