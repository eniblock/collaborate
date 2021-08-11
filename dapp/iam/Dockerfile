FROM maven:3.6.3-jdk-11-slim as build
WORKDIR /workspace/app

COPY pom.xml iam/
COPY authenticator/pom.xml iam/authenticator/
RUN mvn --batch-mode dependency:go-offline install -DskipTests -B -f iam

COPY authenticator/src iam/authenticator/src
RUN mvn --batch-mode install -DskipTests -f iam/authenticator

FROM registry.gitlab.com/the-blockchain-xdev/xdev-product/enterprise-business-network/keycloak:develop.53
COPY --from=build /workspace/app/iam/authenticator/target/authenticator-email.jar /opt/bitnami/keycloak/standalone/deployments/authenticator-email.jar
COPY custom-theme/custom-keycloak/ /opt/bitnami/keycloak/themes/custom-keycloak/
COPY custom-theme/logical-pictures/ /opt/bitnami/keycloak/themes/logical-pictures/
COPY custom-theme/collaborate/ /opt/bitnami/keycloak/themes/collaborate/
COPY realm-config /realm-config/