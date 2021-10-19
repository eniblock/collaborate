[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=bugs)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=code_smells)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=ncloc)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=security_rating)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=sqale_index)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=xdev-tech_collaborate&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=xdev-tech_collaborate)


# Collaborate


## Development

### Requirements

- [Docker](https://docs.docker.com/engine/install/#server)
- [clk k8s](https://github.com/click-project/clk_recipe_k8s)

Install your local kubernetes cluster with:

```shell script
sudo apt-get install pip
curl -sSL https://clk-project.org/install.sh | env CLK_EXTENSIONS=k8s bash
clk k8s flow
```

### Updates
```shell
clk extension update k8s
clk k8s flow
```

### Start the application

```shell script
tilt up
```

### Stop the application

```shell script
tilt down
```

The option `--no-volumes` can be used to keep the volumes.

Note: you need to stop and restart the application to work on the initialization of keycloak.

### Access the application

The application is available on `https://col.localhost`.

Maildev is available on ``http://localhost:1080`.

### Debugging

#### Java API

* In your IDE configure a _remote JVM debug_ configuration, for an example in IntelliJ:
  * Edit Configurations... / Remote JVM Debug
  * Specify the command line: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001`
  * OK
  * Then, select the created run configuration and click on the Debug button

### Testing
#### Postman
Postman collections contains some file data, configure your workspace:

* Open Postman
* File > Settings > General
  * Working directory > Location: _select ths postman directory of this repository_

#### Newman
##### Setup
`sudo npm install -g newman`

##### Run
Following command will execute the `"COL-148 As a DSP, I want to create a BasicAuth Datasource"` requests folder using the `col.localhost.postman_environment.json`environment:
`newman run Collaborate.postman_collection.json -e col.localhost.postman_environment.json --folder "COL-148 As a DSP, I want to create a BasicAuth Datasource" --insecure`

### Credentials

#### Vault

use token `myroot`

#### RabbitMQ

Credentials for the administration webUI can be found from Lens in the `col-tag-rabbitmq-0` environement variables: `RABBITMQ_USERNAME` and `RABBITMQ_PASSWORD`.
For the dev environment: user is `user` and password is `e231219990650321231f`
