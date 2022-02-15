# Fake data sources

To illustrate data source usage, XDev provides two public simple APIs that you can use for testing
your first data source creations depending on the type of asset: Business data or Digital Passport (
also referred as Digital Twin).

* Business data.
  A [fake-datasource-dsp-a.json](../postman/data/datasource/fake-datasource-dsp-a.json) is provided
  as a valid DatasourceDTO suitable for publishing the Business
  dataset https://dsp-a.fds.pcc.eniblock.fr. Functional journey is based on “Scope-based
  permissions”. For instance, access to all the documents whose scope is referentials.
* Digital passport. A fake-datasource-dsp-b.json is provided as a valid DatasourceDTO suitable for
  publishing the Digital passport
  dataset [fake-datasource-dsp-b.json](../postman/data/datasource/fake-datasource-dsp-b.json), where
  assets are vehicles. NB: This fake data source for digital passport is not well suited to
  illustrate an expected API Access Control Management. Functional user journey is on "
  Resources-based permissions" instead of "Scope based permissions", i.e.: access to all data about
  the vehicle whose identifier is VF1VY0C06UC283811 instead of all data whose scope is battery.

## Fake data source API behavior

### JSON resource description database

The Fake data source APIs are HTTP servers based
on [JSON-server](https://github.com/typicode/json-server). The server exposes resources based on a
json resource description database file available at the following links:

* Fake data source Data Service Provider (DSP)
  A: https://dsp-a.fds.pcc.eniblock.fr/db-datasource-dsp-a.json
* Fake data source Data Service Provider (DSP)
  B: https://dsp-a.fds.pcc.eniblock.fr/db-datasource-dsp-b.json

![Fake Data-sources scopes implementation](images/fake-data-source-scopes.jpg)

### First level resources scopes

Requesting a first level resource will result in a HAL[^2] structured response containing the list
of second level resources in the `$._embedded.assets` field, for an example:

For accessing a first level resource, a JWT[^1] access token with the corresponding scope must be
provided. For an example:

* The _Fake datasource DSP A_
  exposes `GET https://dsp-a.fds.pcc.eniblock.fr/referentials`.
* It requires a JWT access token with the `referentials` scope to get an _HTTP OK 200_
  response.

[^1]: [JSON Web Token](https://jwt.io/)

[^2]: [Hypertext Application Language](https://en.wikipedia.org/wiki/Hypertext_Application_Language)

### Second level resources scopes

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
--data-urlencode 'scope=referentials' 
```

It should return
a [successful access token response](https://www.oauth.com/oauth2-servers/access-tokens/access-token-response/)
which should look like:

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJRTldGSmFIb29HOURGMHJaaFplcExyUXdKWnZpMDlNU2Y2Y1NnbHlnUlBJIn0.eyJleHAiOjE2NDIwMjc2OTUsImlhdCI6MTY0MTk5MTY5NSwianRpIjoiYjI1OGRhMWItZDYzOS00MWI2LTliNzYtZTJmZmFmYTE0MjIzIiwiaXNzIjoiaHR0cHM6Ly9pYW0uZmRzLnBjYy5lbmlibG9jay5mci9hdXRoL3JlYWxtcy9kYXRhc291cmNlIiwic3ViIjoiMTc1YmUwYzItY2FjNS00NTY1LWJhMDItOWMyZDY5Yzc3MDVkIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY29sbGFib3JhdGUiLCJzZXNzaW9uX3N0YXRlIjoiNDg4N2MwYTctM2E4My00NWFmLWEzY2UtNmMwNWExMDJmODZmIiwiYWNyIjoiMSIsInNjb3BlIjoibWV0YWRhdGEgcHJvZmlsZSBlbWFpbCByZWZlcmVudGlhbHMiLCJjbGllbnRIb3N0IjoiMTAuMzAuMC41IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJjbGllbnRJZCI6ImNvbGxhYm9yYXRlIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWNvbGxhYm9yYXRlIiwiY2xpZW50QWRkcmVzcyI6IjEwLjMwLjAuNSJ9.cAVpSBv3qeCNLcf2dcYStxrIdboOW06MBKa58b7qvvvOhEdE6hDZShzRJtvVOLF55jYGflNsSBakF-5ogIUQfMXuOrR5cXepu-V7w89XAZmizzs_UcZAqOamMsNF7Tdp_7ouzqRQhKCjzSvpp6Ic5P-UE3qcjX5t1jBx8dRFRkrmVy9A6hSyWZhFrjS0oydtLF0JkYJp_WDKXUGRxl1-J0JmwuqIYTnESnl4DPE1r7VA5RY30svovMZ9bhUdtObr8R_7NfKWQ8veok64KtV-55FSXxOBowHDbaGYftC5ZwPVo1iHU0iO3f9Ol6OZQCm92WtP-Agzi107WEZiD4ylhg",
  "expires_in": 36000,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzYjQ1OTBjYi1iOWViLTQ2YjYtOTA3OS1hYjMyNDllN2I4YjYifQ.eyJleHAiOjE2NDE5OTM0OTUsImlhdCI6MTY0MTk5MTY5NSwianRpIjoiNDNhOWQzYmItMzc3YS00ZmNkLWEzMTQtOGM0NjYwYjQ2Nzk4IiwiaXNzIjoiaHR0cHM6Ly9pYW0uZmRzLnBjYy5lbmlibG9jay5mci9hdXRoL3JlYWxtcy9kYXRhc291cmNlIiwiYXVkIjoiaHR0cHM6Ly9pYW0uZmRzLnBjYy5lbmlibG9jay5mci9hdXRoL3JlYWxtcy9kYXRhc291cmNlIiwic3ViIjoiMTc1YmUwYzItY2FjNS00NTY1LWJhMDItOWMyZDY5Yzc3MDVkIiwidHlwIjoiUmVmcmVzaCIsImF6cCI6ImNvbGxhYm9yYXRlIiwic2Vzc2lvbl9zdGF0ZSI6IjQ4ODdjMGE3LTNhODMtNDVhZi1hM2NlLTZjMDVhMTAyZjg2ZiIsInNjb3BlIjoibWV0YWRhdGEgcHJvZmlsZSBlbWFpbCByZWZlcmVudGlhbHMifQ.5Sg0qJXMD0_QIqQB_2gyKTK_m-ki50r08HUjaL_PlVQ",
  "token_type": "bearer",
  "not-before-policy": 0,
  "session_state": "4887c0a7-3a83-45af-a3ce-6c05a102f86f",
  "scope": "metadata profile email referentials"
}
```

The `access_token` field value can be used as _Authorization bearer header_ while requesting a fake
data-source resource , for an example `GET https://dsp-a.fds.pcc.eniblock.fr/referentials` to get
all batteries resources:

```
curl --location --request GET 'https://datasource-dsp-b.fake-datasource.localhost/breferentials' \
--header 'Authorization: Bearer XXX_ACCESS_TOKEN_GOES_HERE_XXX'
```

[![](https://mermaid.ink/img/eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgcGFydGljaXBhbnQgQyBhcyBDbGllbnRcbiAgICBwYXJ0aWNpcGFudCBJIGFzIElBTVxuICAgIHBhcnRpY2lwYW50IEYgYXMgRmFrZSBEYXRhIHNvdXJjZVxuICAgIEMgLT4-KyBJOiBHRVQgSldUIGZvciBzY29wZSAncmVmZXJlbnRpYWxzJ1xuICAgIE5vdGUgbGVmdCBvZiBDOiBQT1NUIC9hdXRoL3JlYWxtcy9kYXRhc291cmNlL3Byb3RvY29sL29wZW5pZC1jb25uZWN0L3Rva2VuIEhUVFAvMS4xIDxici8-SG9zdDogaWFtLmZkcy5wY2MuZW5pYmxvY2suZnI8YnIvPmdyYW50X3R5cGU9Y2xpZW50X2NyZWRlbnRpYWxzPGJyLz4mY2xpZW50X2lkPWNvbGxhYm9yYXRlPGJyLz4mY2xpZW50X3NlY3JldD01NzkzNmIxNi0yZjkwLTQzNGEtOWViNC04NDM2NjhhM2E1MjE8YnIvPnNjb3BlPXJlZmVyZW50aWFsc1xuXG4gICAgSSAtLT4-LSBDOiBKV1RcbiAgICBOb3RlIHJpZ2h0IG9mIEk6IFwiYWNjZXNzX3Rva2VuXCI6IFwiZXlKaC4uLlwiPGJyLz5bLi4uXTxici8-XCJzY29wZVwiOiBcInJlZmVyZW50aWFsc1wiPGJyLz5cblxuICAgIEMgLT4-KyBGOiBHRVQgL3JlZmVyZW50aWFscyB3aXRoIEpXVFxuICAgIE5vdGUgbGVmdCBvZiBDOiBHRVQgL3JlZmVyZW50aWFscyBIVFRQLzEuMSA8YnIvPkhvc3Q6IGRzcC1hLmZkcy5wY2MuZW5pYmxvY2suZnI8YnIvPkF1dGhvcml6YXRpb246IEJlYXJlciBleUpoLi4uXG4gICAgXG4gICAgRiAtPj4rIEk6IHZlcmlmeSB0b2tlblxuICAgIEkgLS0-Pi0gRjogdXNlclxuICAgIGFsdCB1c2VyIGhhcyBzY29wZSAncmVmZXJlbnRpYWxzJ1xuICAgICAgRi0tPj5DOiBIVFRQIDIwMCBPSyAgXG4gICAgZWxzZVxuICAgICAgRi0tPj4tQzogSFRUUCA0MDMgRm9yYmlkZGVuXG4gICAgZW5kXG5cblxuIiwibWVybWFpZCI6eyJ0aGVtZSI6ImRlZmF1bHQifSwidXBkYXRlRWRpdG9yIjpmYWxzZSwiYXV0b1N5bmMiOnRydWUsInVwZGF0ZURpYWdyYW0iOmZhbHNlfQ)](https://mermaid.live/edit#eyJjb2RlIjoic2VxdWVuY2VEaWFncmFtXG4gICAgcGFydGljaXBhbnQgQyBhcyBDbGllbnRcbiAgICBwYXJ0aWNpcGFudCBJIGFzIElBTVxuICAgIHBhcnRpY2lwYW50IEYgYXMgRmFrZSBEYXRhIHNvdXJjZVxuICAgIEMgLT4-KyBJOiBHRVQgSldUIGZvciBzY29wZSAncmVmZXJlbnRpYWxzJ1xuICAgIE5vdGUgbGVmdCBvZiBDOiBQT1NUIC9hdXRoL3JlYWxtcy9kYXRhc291cmNlL3Byb3RvY29sL29wZW5pZC1jb25uZWN0L3Rva2VuIEhUVFAvMS4xIDxici8-SG9zdDogaWFtLmZkcy5wY2MuZW5pYmxvY2suZnI8YnIvPmdyYW50X3R5cGU9Y2xpZW50X2NyZWRlbnRpYWxzPGJyLz4mY2xpZW50X2lkPWNvbGxhYm9yYXRlPGJyLz4mY2xpZW50X3NlY3JldD01NzkzNmIxNi0yZjkwLTQzNGEtOWViNC04NDM2NjhhM2E1MjE8YnIvPnNjb3BlPXJlZmVyZW50aWFsc1xuXG4gICAgSSAtLT4-LSBDOiBKV1RcbiAgICBOb3RlIHJpZ2h0IG9mIEk6IFwiYWNjZXNzX3Rva2VuXCI6IFwiZXlKaC4uLlwiPGJyLz5bLi4uXTxici8-XCJzY29wZVwiOiBcInJlZmVyZW50aWFsc1wiPGJyLz5cblxuICAgIEMgLT4-KyBGOiBHRVQgL3JlZmVyZW50aWFscyB3aXRoIEpXVFxuICAgIE5vdGUgbGVmdCBvZiBDOiBHRVQgL3JlZmVyZW50aWFscyBIVFRQLzEuMSA8YnIvPkhvc3Q6IGRzcC1hLmZkcy5wY2MuZW5pYmxvY2suZnI8YnIvPkF1dGhvcml6YXRpb246IEJlYXJlciBleUpoLi4uXG4gICAgXG4gICAgRiAtPj4rIEk6IHZlcmlmeSB0b2tlblxuICAgIEkgLS0-Pi0gRjogdXNlclxuICAgIGFsdCB1c2VyIGhhcyBzY29wZSAncmVmZXJlbnRpYWxzJ1xuICAgICAgRi0tPj5DOiBIVFRQIDIwMCBPSyAgXG4gICAgZWxzZVxuICAgICAgRi0tPj4tQzogSFRUUCA0MDMgRm9yYmlkZGVuXG4gICAgZW5kXG5cblxuIiwibWVybWFpZCI6IntcbiAgXCJ0aGVtZVwiOiBcImRlZmF1bHRcIlxufSIsInVwZGF0ZUVkaXRvciI6ZmFsc2UsImF1dG9TeW5jIjp0cnVlLCJ1cGRhdGVEaWFncmFtIjpmYWxzZX0)
