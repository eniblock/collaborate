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

## Overall Solution

The main goal of Passport for Connected Car (PCC) is to bring more health to existing markets  - for example by ensuring the reliability of the residual value of the car on the second-hand market - but it also opens up new opportunities by building the grounds for a large, secure, and fair market data platform. Information that was previously collected individually in silos by each market participant, can now be shared in a collaborative approach, allowing to build new services such as pay-as-you-go insurance solutions, or predictive maintenance offers.
The blockchain technology ensures the security and sovereignty of the collected data, and allows an efficient and fair re-distribution of the value created using tokens and will drive following benefits for the whole ecosystem :

* Ensure end to end car data *certification and traceability* during whole car life cycle.
* Ease car technical *data exchange* between all the stakeholder.
* Enable *new services opportunities* such as predictive maintenance or pay how you drive insurance by leveraging certified data.

Streamline end user car passport application development enabling car data (administrative, technical, regulatory) follow up and management.

## Leveraging The Blockchain Xdev - Passport for Connected Car

The ambition of this product is to foster collaboration within the digital asset ecosystem by providing a backbone for all asset stakeholders implementing asset single source of truth, *GDPR compliant data sharing* and built-in data monetization capabilities in order to improve asset supply chain process and life cycle services by unleashing trustful data sharing.

![overall solution](doc/images/overall-solution.png)

Our solution can be understood in three layers: an *Enterprise Business Network*, A *Trustful Data Protocol*, and the *Applications and Multi party Services Marketplace*. Each component plays a distinct role allowing members to derive the most value for their businesses.

### Asset Enterprise Business Network

The foundation of the solution is its business network: from asset design to asset user or operator. Each party shares information that can be tracked, stored and actioned across the platform throughout an digital passport.

### Asset Data Protocol

Our solution Protocol is accessible via an open Application Programming Interface (API) and brings together the business network through a set of open standards. Powered by blockchain technology, the platform enables the industry to share information and collaborate securely.

### Asset Apps and Multiparty services

An open Applications and Multiparty Services Marketplace allows both consortium and third parties to publish fit-for-purpose services atop the Protocol, fostering innovation and value creation.

## Technological orientations

The car data network and protocols are brought to life by many interacting software components, organized in five layers: Application, Service, Access, Infrastructure, and Blockchain.

![Functional architecture of the PCC platform](doc/images/architecture.png)

### Application Layer

The application layer is composed of the Passport APP and the Governance APP.

#### Passport APP

This application allows end-users to the consultation and the update of the passport conveyed by the end-users.

Services accessible to end-user through this application are:

* Manage access right (grant/remove) to vehicle passport data.
* Find out the car’s authentic metric (i.e. odometer readings) at different times.
* Get an in-depth summary of the car health (accidents, damages)

#### Governance APP

This application is dedicated to the consortium members. It offers an interface and functionalities for monitoring and managing platform data (e.g. data sources and data storage management, access control, and monitoring of revenue related to data valuation).

### Service Layer

Several APIs will be provided :

* Asset-Vehicle life cycle API[^1] : Allows the creation, consultation and the update of the digital passport conveyed by digital passport owner or by any other asset stakeholder data provider (e.g. invoice to be added to a repair visit).

* Security API[^1] : This service, used by the platform users, offers mechanisms to manage identities and access control. Services accessible to end-user through this application are:
  * Identity Federation or Broker: An intermediary service that connects multiple service providers with different identity providers. As an intermediary service, the identity broker is responsible for creating a trust relationship with an external identity provider in order to use its identities to access internal services exposed by service providers.

* Partners API[^1] :provides an HTTP API to consortium members only,  for interacting with data coming from the digital passport (on-chain) or directly from the system of another Partner system (off-chain). This process will be secured by token authorization mechanism.

* Analytics API[^1]:  Store (off-chain) and manage business KPI and analytic about the data assets available in the marketplace, like the digital passport.

* Data Marketplace API[^2]: provides an HTTP API to all network participants,  for interacting with data coming from the digital passport (on-chain) or directly from the system of another Partner system (off-chain). This process will be secured by an end-to-end encryption environment. Services accessible to end-user through this application are:
  * Manage access right (grant/remove) to partner data.
  * Access anonymous data to analyze business performance. (i.e. number of accidents per vehicle brand, number of accidents by age rates).
  * Generated verified reports with their own data or supplement those reports with to PCC data to sell it in to other partners by using the marketplace.

* Governance API[^2]: allow the management of the Data platform and access control, accessible for the administrator profiles.

* Notification API[^2] : API that manages the interaction between the platform data and external consortium systems for full enterprise integration. The most basic scenario is to notify data owners on the activity of their data in real-time.

[^1]: MVP Perimeter
[^2]: Future version Perimeter

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
