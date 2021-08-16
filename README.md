# Collaborate

## Requirements

- [Docker](https://docs.docker.com/engine/install/#server)
- [clk k8s](https://github.com/click-project/clk_recipe_k8s)

## Installation

Install your local kubernetes cluster with:

```shell script
sudo apt-get install pip
curl -sSL https://bit.ly/3ii011L | env CLK_EXTENSIONS=k8s bash
clk k8s install-dependency k3d
clk k8s flow
```


Give access to gitlab registries to your local kubernetes instance by generating an access token with the `read_api`
scope at https://gitlab.com/-/profile/personal_access_token, and running

```bash
kubectl create secret docker-registry gitlab-registry --docker-server=registry.gitlab.com --docker-username=$GITLAB_USER --docker-password=$GITLAB_TOKEN
```

### Migration from v0.2.0
Delete the `*.tgz`files from `collaborate/helm/collaborate-dapp/charts` folder


## Start the application

```shell script
tilt up
```

## Stop the application

```shell script
tilt down
```

The option `--no-volumes` can be used to keep the volumes.

Note: you need to stop and restart the application to work on the initialization of keycloak.

## Access the application

The application is available on `https://col.localhost`.

Maildev is available on ``http://localhost:1080`.

# Debugging
## Java API
To enable _remote_ debug on Java dockerized application:
* `"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5000"` argument has to be given to the JVM in charge of executing the Java application in the container.
  _Where `5000` is the debug port_
* The `Tiltfile` has to bind the container debug port to a local port (see: [Tilt API doc](https://docs.tilt.dev/api.html)), for an example:
```python
k8s_resource(
    'collaborate-dapp-api',
    port_forwards=['5001:5000']
)
```
* In your IDE configure a _remote JVM debug_ configuration, for an example in IntelliJ:
  * Edit Configurations... / Remote JVM Debug
  * Specify the command line: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001`
  * OK
  * Then, select the created run configuration and click on the Debug button

# Testing
## Postman
Postman collections contains some file data, configure your workspace:

* Open Postman
* File > Settings > General
  * Working directory > Location: _select ths postman directory of this repository_  

## Newman
### Setup
`sudo npm install -g newman`

### Run
Following command will execute the `"COL-148 As a DSP, I want to create a BasicAuth Datasource"` requests folder using the `col.localhost.postman_environment.json`environment:  
`newman run Collaborate.postman_collection.json -e col.localhost.postman_environment.json --folder "COL-148 As a DSP, I want to create a BasicAuth Datasource" --insecure`
