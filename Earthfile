VERSION 0.6


test:
    BUILD ./dapp/api+test
    BUILD ./dapp/iam+test
    BUILD ./helm+lint

docker:
    ARG tag
    BUILD ./dapp/api+docker --tag=${tag}
    BUILD ./dapp/iam+docker --tag=${tag}

sonar:
    FROM sonarsource/sonar-scanner-cli
    RUN apk add yq
    RUN git config --global --add safe.directory /usr/src
    COPY . ./
    COPY --if-exists .git .git
    COPY ./dapp/api+build/BOOT-INF/classes classes
    RUN echo sonar.projectVersion=$(yq eval .version helm/collaborate/Chart.yaml) >> sonar-project.properties
    ENV SONAR_HOST_URL=https://sonarcloud.io
    RUN --mount=type=cache,target=/opt/sonar-scanner/.sonar/cache \
        --secret GITHUB_TOKEN \
        --secret SONAR_TOKEN \
        sonar-scanner \
        -D sonar.java.binaries=classes
