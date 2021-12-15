## Creating NFT Token for assets

During the creation process, when it contains personal data the owner can flag the data source. This
flag is used to define the NTF minting workflow:

```mermaid
graph TD
    A[Datasource creation]
    A --> B{contains personal data ?}
    B -->|Yes| C[Digital passport]
    
    C --> D[Create digital passport for an user asset <br/> ex: a vehicle]
    D --> E{User consent}
    E -->|Yes | F[Mint digital passport NFT] 
    

    B -->|No| G[Business dataset]
    G --> H[Mint an NFT by scope]
```

As you can see, the differences between digital passport for personal data and business dataset is:

* With personal data:
    * The data source owner needs to ask a user to consent to the **digital passport NFT** mint by
      calling `POST api/v1/digital-passport`.
    * The user can consent by calling `POST /api/v1/digital-passport/multisig/{contract-id}`. This
      consent triggers the digital-passport NFT mint, representing a user asset.
* With business dataset:
    * No consent is required, the owner mint directly a **business dataset NFT** for each scope of
      the created data source.

When a NFT is minted, it will contain metadata containing information about the data source it is
associated to: the **NFT Catalog**.

```mermaid
classDiagram
    NFT *-- NFT Catalog
    NFT Catalog --* Data source Configuration
```
