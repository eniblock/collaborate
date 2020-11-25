FROM jboss/keycloak
USER root
RUN curl -sL https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-alpine-linux-amd64-v0.6.1.tar.gz | tar xvzC /usr/bin
USER 1000
COPY certs/ /etc/x509/https/
COPY realm-config/realm-export.json /realm-export.json.tpl
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

ENTRYPOINT []
CMD ["dockerize", "-template", "/realm-export.json.tpl:/tmp/realm-export.json", \
     "/opt/jboss/tools/docker-entrypoint.sh", \
     "-b", "0.0.0.0", \
     "-Dkeycloak.migration.action=import", \
     "-Dkeycloak.migration.provider=singleFile", \
     "-Dkeycloak.migration.file=/tmp/realm-export.json", \
     "-Dkeycloak.migration.strategy=IGNORE_EXISTING"]