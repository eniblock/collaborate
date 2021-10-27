# Fake Datasource
This aims to emulate a datasource to be used in the PCC project. 
This fake datasource starts a web server that serves metadata (that you can download) in json+hal protected by an authorization server (keycloak).

## Start the datasource
```shell
docker-compose up -d
``` 

## Stop the datasource
```shell
docker-compose down -v
```

## Authentication
In order to use the datasource, you have to authenticate against keycloak using OAuth 2.0 Client Credentials Grant.
To obtain an access token, you need the client id and secret and scopes. There are 4 different scopes:

- `metadata:read` to be able to list the metadata
- `maintenance-plans:read` to be able to get & download the metadata related to `maintenance-plans` 
- `insurance-offers:read` to be able to get & download the metadata related to `insurance-offers`
- `insurance-policies:read` to be able to get & download the metadata related to `insurance-policies`
- `maintenance-reports:read` to be able to get & download the metadata related to `maintenance-reports`

Here an example using curl and the default client id and secret:
```shell
curl --location --request POST 'http://localhost:9080/auth/realms/datasource/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_id=collaborate' \
--data-urlencode 'client_secret=57936b16-2f90-434a-9eb4-843668a3a521' \
--data-urlencode 'scope=maintenance-plans-Peugeot-3008 maintenance-plans-Peugeot-5008'
```

## Datasource API
Once you obtain an access token, you call the metadata list with (replace `yourtoken` with your token).
The scope `metadata:read` is required.
```shell
curl --location --request GET 'http://localhost:3000/metadata' \
--header 'Authorization: Bearer yourtoken'
```

Use the following to request the detail of a metadata.
The scope of the metadata (for example `maintenance-plans:read` for the `metadata 1`) is required.
```curl
curl --location --request GET 'http://localhost:3000/metadata/1' \
--header 'Authorization: Bearer yourtoken'
```

Same to download a metadata.
The scope of the metadata (for example `maintenance-plans:read` for the `metadata 1`) is required.
```shell
curl --location --request GET 'http://localhost:3000/metadata/1/download' \
--header 'Authorization: Bearer yourtoken'
```
