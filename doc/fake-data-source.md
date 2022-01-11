# Fake data sources

To illustrate data source usage, XDev provide two public simple API that you can use for testing
your first data source creations.

* [fake-datasource-dsp-a.json](../postman/data/datasource/fake-datasource-dsp-a.json) is provided as
  a valid DatasourceDTO suitable for working with _Business
  dataset_ https://dsp-a.fds.pcc.eniblock.fr data source usable for _Business dataset_ data source.
* [fake-datasource-dsp-b.json](../postman/data/datasource/fake-datasource-dsp-b.json) is provided as
  a valid DatasourceDTO suitable for working with _Digital
  passport_ https://dsp-b.fds.pcc.eniblock.fr data source, where assets are vehicles.
  _**NB**: This fake data source for digital passport is not well suited to illustrate an expected
  API Access Control Management. Functional user journey are based on "Resources-based permissions"
  instead of "Scope based permissions", ie: access on all data about the asset `VF1VY0C06UC283811`
  instead of all data about `battery`_

## Fake data source API behavior

### JSON resource description database

The Fake data source APIs are HTTP servers based
on [JSON-server](https://github.com/typicode/json-server). The server exposes resources based on a
json resource description database file available at the following links:

* Fake datasource DSP A: https://dsp-a.fds.pcc.eniblock.fr/db-datasource-dsp-a.json
* Fake datasource DSP B: https://dsp-a.fds.pcc.eniblock.fr/db-datasource-dsp-b.json

### First level resources

For accessing a first level resource, a JWT[^1] access token with the corresponding scope must be
provided. For an example:

* The _Fake datasource DSP A_
  exposes `GET https://dsp-a.fds.pcc.eniblock.fr/referentials`.
* It requires a JWT access token with the `referentials` scope to get an _HTTP OK 200_
  response.

Requesting a first level resource will result in a HAL[^2] structured response containing the list
of second level resources in the `$._embedded.assets` field.

[^1]: [JSON Web Token](https://jwt.io/)

[^2]: [Hypertext Application Language](https://datasource-dsp-a.fake-datasource.localhost/referentials)

### Second level resources

For accessing a second level resource, a JWT access token with the defined scope must be provided.
For an example:

* If the database description file is defined as:
   ```json
  { "referentials": [{
      "id": "1",
      "title": "Maintenance plans A",
      "scope": "maintenance-plans-A",
      "content": [
        {
          "name": "Maintenance plans A",
          "scheduled": "2022-01-11T17:38:36Z"
        }]
    }]
  }
  ```
* The first level resource is `GET referentials`, the `GET referentials/1` is the second level
  resource.
* For getting an _HTTP OK 200_ response from the `GET referentials/1` second level resource, the
  scope `maintenance-plans-A` is required

## Get a JWT for a given scope

The fake data sources use a single IAM server for providing access token
implementing [OAuth2 Client credentials workflow](https://www.oauth.com/oauth2-servers/access-tokens/client-credentials/)

```shell
curl --location --request POST 'https://iam.fds.pcc.eniblock.fr/auth/realms/datasource/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_id=collaborate' \
--data-urlencode 'client_secret=57936b16-2f90-434a-9eb4-843668a3a521' \
--data-urlencode 'scope=battery' 
```

Then use this `access_token` on `GET https://dsp-b.fds.pcc.eniblock.fr/battery` to get all batteries
resources:

```
curl --location --request GET 'https://datasource-dsp-b.fake-datasource.localhost/battery/VF1VY0C06UC283811' \
--header 'Authorization: Bearer XXX_ACCESS_TOKEN_GOES_HERE_XXX' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_id=collaborate' \
--data-urlencode 'client_secret=57936b16-2f90-434a-9eb4-843668a3a521'
```
