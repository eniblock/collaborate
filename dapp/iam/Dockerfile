#
# Build event listener
#
FROM maven:3.6.3-jdk-11-slim AS KeycloakExtension
COPY kc-extension/src /home/app/src
COPY kc-extension/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true

FROM jboss/keycloak:11.0.3
USER root
RUN curl -sL https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-alpine-linux-amd64-v0.6.1.tar.gz | tar xvzC /usr/bin
USER 1000
COPY certs/ /etc/x509/https/
COPY realm-config/realm-export.json /realm-export.json.tpl
COPY standalone/standalone-ha.xml /opt/jboss/keycloak/standalone/configuration/standalone-ha.xml
COPY --from=KeycloakExtension /home/app/src/kc-extension-ear/target/kc-extension-ear.ear /opt/jboss/keycloak/standalone/deployments/

# the SMTP configuration variables
# SMTP_AUTH, SMTP_STARTTLS and SMTP_SSL are boolean that should be either true, false or an empty string
ENV SMTP_FROM=noreply@theblockchainxdev.com \
  SMTP_HOST=maildev \
  SMTP_AUTH= \
  SMTP_STARTTLS= \
  SMTP_SSL= \
  SMTP_USER= \
  SMTP_PASSWORD=
# to be activated for dev only
ENV INSERT_TEST_USERS=false
ENV REALM=collaborate \
  KEYCLOAK_IMPORT=/realm-export.json \
  KEYCLOAK_USER=kcadmin \
  KEYCLOAK_PASSWORD=kcadmin

ENV GOOGLE_AUTH_CLIENT_SECRET=G9j2wLI3E-a97t8T5XG8BGA9 \
  GOOGLE_AUTH_CLIENT_ID=1068446898590-lpk3gcli4h6qokoei6pqkmdolpdugeau.apps.googleusercontent.com

ENV IDP_ADMIN_ROLE=service_identity_provider_administrator
ENV ADMIN_ROLE=service_provider_administrator

ENTRYPOINT []
CMD ["dockerize", "-template", "/realm-export.json.tpl:/tmp/realm-export.json", \
     "/opt/jboss/tools/docker-entrypoint.sh", \
     "-b", "0.0.0.0", \
     "-Dkeycloak.migration.action=import", \
     "-Dkeycloak.migration.provider=singleFile", \
     "-Dkeycloak.migration.file=/tmp/realm-export.json", \
     "-Dkeycloak.migration.strategy=IGNORE_EXISTING"]