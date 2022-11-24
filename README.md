[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=bugs)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=code_smells)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=ncloc)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=security_rating)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=sqale_index)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=eniblock_collaborate&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=eniblock_collaborate)

# Collaborate

## Overall Solution

The main goal of Passport for Connected Car (PCC) is to bring more health to existing markets - for
example by ensuring the reliability of the residual value of the car on the second-hand market - but
it also opens up new opportunities by building the grounds for a large, secure, and fair market data
platform. Information that was previously collected individually in silos by each market
participant, can now be shared in a collaborative approach, allowing to build new services such as
pay-as-you-go insurance solutions, or predictive maintenance offers. The blockchain technology
ensures the security and sovereignty of the collected data, and allows an efficient and fair
re-distribution of the value created using tokens and will drive following benefits for the whole
ecosystem :

* Ensure end to end car data *certification and traceability* during whole car life cycle.
* Ease car technical *data exchange* between all the stakeholder.
* Enable *new services opportunities* such as predictive maintenance or pay how you drive insurance
  by leveraging certified data.

Streamline end user car passport application development enabling car data (administrative,
technical, regulatory) follow up and management.

## Leveraging The Blockchain Xdev - Passport for Connected Car

The ambition of this product is to foster collaboration within the digital asset ecosystem by
providing a backbone for all asset stakeholders implementing asset single source of truth, *GDPR
compliant data sharing* and built-in data monetization capabilities in order to improve asset supply
chain process and life cycle services by unleashing trustful data sharing.

![overall solution](doc/images/overall-solution.png)

Our solution can be understood in three layers: an *Enterprise Business Network*, A *Trustful Data
Protocol*, and the *Applications and Multi party Services Marketplace*. Each component plays a
distinct role allowing members to derive the most value for their businesses.

### Asset Enterprise Business Network

The foundation of the solution is its business network: from asset design to asset user or operator.
Each party shares information that can be tracked, stored and actioned across the platform
throughout an digital passport.

### Asset Data Protocol

Our solution Protocol is accessible via an open Application Programming Interface (API) and brings
together the business network through a set of open standards. Powered by blockchain technology, the
platform enables the industry to share information and collaborate securely.

### Asset Apps and Multiparty services

An open Applications and Multiparty Services Marketplace allows both consortium and third parties to
publish fit-for-purpose services atop the Protocol, fostering innovation and value creation.

## Technological orientations

The car data network and protocols are brought to life by many interacting software components,
organized in five layers: Application, Service, Access, Infrastructure, and Blockchain.

![Functional architecture of the PCC platform](doc/images/architecture.png)

### Application Layer

The application layer is composed of the Passport APP and the Governance APP.

#### Passport APP

This application allows end-users to the consultation and the update of the passport conveyed by the
end-users.

Services accessible to end-user through this application are:

* Manage access right (grant/remove) to vehicle passport data.
* Find out the car’s authentic metric (i.e. odometer readings) at different times.
* Get an in-depth summary of the car health (accidents, damages)

#### Governance APP

This application is dedicated to the consortium members. It offers an interface and functionalities
for monitoring and managing platform data (e.g. datasources and data storage management, access
control, and monitoring of revenue related to data valuation).

### Service Layer

Several APIs will be provided :

* Asset-Vehicle life cycle API[^1] : Allows the creation, consultation and the update of the digital
  passport conveyed by digital passport owner or by any other asset stakeholder data provider (e.g.
  invoice to be added to a repair visit).

* Security API[^1] : This service, used by the platform users, offers mechanisms to manage
  identities and access control. Services accessible to end-user through this application are:
    * Identity Federation or Broker: An intermediary service that connects multiple service
      providers with different identity providers. As an intermediary service, the identity broker
      is responsible for creating a trust relationship with an external identity provider in order
      to use its identities to access internal services exposed by service providers.

* Partners API[^1] :provides an HTTP API to consortium members only, for interacting with data
  coming from the digital passport (on-chain) or directly from the system of another Partner
  system (off-chain). This process will be secured by token authorization mechanism.

* Analytics API[^1]:  Store (off-chain) and manage business KPI and analytic about the data assets
  available in the marketplace, like the digital passport.

* Data Marketplace API[^2]: provides an HTTP API to all network participants, for interacting with
  data coming from the digital passport (on-chain) or directly from the system of another Partner
  system (off-chain). This process will be secured by an end-to-end encryption environment. Services
  accessible to end-user through this application are:
    * Manage access right (grant/remove) to partner data.
    * Access anonymous data to analyze business performance. (i.e. number of accidents per vehicle
      brand, number of accidents by age rates).
    * Generated verified reports with their own data or supplement those reports with to PCC data to
      sell it in to other partners by using the marketplace.

* Governance API[^2]: allow the management of the Data platform and access control, accessible for
  the administrator profiles.

* Notification API[^2] : API that manages the interaction between the platform data and external
  consortium systems for full enterprise integration. The most basic scenario is to notify data
  owners on the activity of their data in real-time.

[^1]: MVP Perimeter
[^2]: Future version Perimeter

### Access Layer

This layer provides access mechanisms to the infrastructure layer for APIs of the service layer.

#### Data API Gateway

It provides mechanisms to access the datasources of all the consortium members and the Data Storage
of the same consortium member. In order to achieve interoperability and adaptability goals, several
connectors for the most common datasources ( Web Services, Relational Database, Files systems) are
provided. In order to guarantee the security and the privacy of the system, any data is duplicated,
notarization services based on fingerprint mechanism are used.

#### Custody API

It provides mechanisms to access the Key Storage and to sign blockchain transactions.

#### Policy Manager

It provides mechanisms for controlling access to datasources resources.

#### Blockchain API Gateway

It provides APIs to interact with the most common public blockchain: Tezos and Etherieum. It
includes services like

* Rest API for calling Smart contracts entry points
* Generic API for Transaction forge
* Generic API for Transaction submission using a pool of blockchain RPC gateways
* Generic API for Transaction Confirmation using a pool of indexer.

### Infrastructure Layer

All infrastructure components related to PCC platform are represented in this layer:

* Data cache and load management: Data from the Access layer is persisted into a local cache (Rabbit
  MQ, Local DB) for short durability and deleted after no more than X days. Business privacy
  contents are assured by Data encryption and Data partition by client channels.
* Decentralized File System: Some data will only store a fingerprint of its content in the
  smart-contrat and a link to get the data via a decentralized File System. So smart-contract
  storage size and cost can be reduced and each consumer would be able to ensure that data has not
  been maliciously altered.
* Key storage : Secure, store and tightly control access to tokens, passwords, certificates,
  encryption keys for protecting secrets and other sensitive data using an HTTP API.

### Blockchain Layer

#### Asset management Smart contracts

Four Main Smart contract for asset management are defined :

* Asset Digital Passport : define the set of data (or data model) as a Non Fungible token,
  implemented following the tezos FA 2.0)  which can be,
    * A simple and generic structure with a digital fingerprint of the asset data and metadata
      stored in a third party system. (blockchain-based timestamping)
    * A simple table of attributes of an asset. In the case of a car : Vin, Model, Color then will
      be stored in a common golden data (blockchain ledger) and a digital fingerprint of the asset
      metadata stored in a third party system (blockchain-based timestamping).
    * A more complex structure that represents the whole Life cycle of an Asset based on Event,
      Metric, etc. In the case of a car, Maintenance Event with Kilometer and Pieces state metrics.

* Asset Access Management smart contract : Asset Digital Passport is highly coupled with the Asset
  Access Management smart contract, composed of set of rules for ensuring compliance with GDPR
  regulation for vehicle passports data, like :
    * Role-based access management : Define Participants (parties) and their role over the Asset.
    * Consent Management : responsible for managing data owner personal data disposal policies and
      the corresponding consents, including generation, updates and duplication.
    * Data access registry : keep a full registry of data owner consent for data access and usage.

* Asset Monetization - PCC Token : smart contract for Asset data monetization defined as an tezos FA
  1.2 Utility token. These tokens will be associated, with Asset Digital Passport, for passport data
  exchange.

* Governance - Golden Token : smart contract enabling consortium members to manage the governance of
  the solution in proportion to their respective contributions.

For details about how Token will be used in the PCC platform, refer to section Use of tokens

## Solution Value Creation

### Use of tokens

In the context of the PCC solution, three distinct tokens are defined. This distinction ensures the
tokenization of the vehicle as an asset token, the management of the economic flows related to the
PCC solution's services thanks to a reward token, and the governance of the solution between the
industrial members on the other using a governance token.

![PCC tokens and functionalities](doc/images/use-of-tokens.png)

#### Asset Token - PCC Vehicle Data Passport

The Asset Token is a digital token used to represent verifiable proofs of authenticity and ownership
of a real asset. In the case of complex real assets such as real estate or car, the asset token is a
non-fungible token composed of a set of data collected and handled by various stakeholders (issuer,
designer, suppliers) as depicted in table 1.

The concept of asset digital passport aim at gathering all verifiable and certified data describing
asset and associated management rules into one blockchain-based structure composed of:

* A Non-fungible token, following the standard FA1.2, representing the real asset and updated at
  each step of its lifecycle and composed by data collected, stored at each stakeholder data
  warehouse.
* An Asset Access Smart Contract defining conditions and rules for each asset stakeholder to certify
  access, authorization to use, or manage digital passport data. Taking into account, any usage of
  vehicle data must be compliant with GDPR regulations, the core part of this contract is the
  management of owner consent and complete access traceability.

![](doc/images/use-of-tokens-2.png)

#### Reward Token - PCC token

The PCC_Token is designed to allow both individual users and industrial members to:

* Value the creation and contribution to the Vehicle Passport application.
* Offer discounts or rewards for subscribing to services in exchange for sharing vehicle data.
* Ensure the distribution of the value of a service between the different contributors within the
  PCC platform.

![PCC_Token Usage Scenarios](doc/images/use-of-tokens-3.png)

The PCC_Token will be at the heart of new economic exchanges between vehicle owners or drivers and
the industrial stakeholders. All these new transactions will be based on the use of the vehicle's
passport app and access to new personalized services. Figure 12 (below) illustrates some scenarios
for using the PCC solution based on PCC_Token transactions.

#### Gouvernance Token - PCC Gold token

The PCC_Gold aims to enable consortium members t manage the governance of the solution in proportion
to their respective contributions by :

* Business network management: define Participants ( parties) and their role on the blockchain.
* Token management: Manage all operational and usage aspects of the PCC_Token, Asset token, and Gold
  token itself.

The PCC _Gold is used in all operations involving a collaborative decision. The decision-making
process requires the vote of each consortium member. To ensure a fair and equitable representation
of each consortium member, each voter's weight is proportional to the amount of PCC _Gold at his
disposal.

![PCC_Gold Token usage scenarios](doc/images/use-of-tokens-4.png)

## API usages

![Data gateway overview](doc/images/data-gateway.png)

The [Collaborate Open API](https://collaborate.api.bxdev.tech/) is updated after each merge on the
develop branch of the project repository.

Detailed usage about:

* [IAM: Identity Access Management](doc/iam.md)
* [(1) Configuring datasources](doc/datasource.md)
* [(2) Creating NFT Token for assets](doc/create-nft.md)
* [(3) Consuming NFT Token associated data](doc/access-nft-dataset-catalog.md)

see also:

* [Accessing and testing the Collaborate application with Postman](doc/postman.md)

## [Run your environment](doc/setup-dev-env.md)
