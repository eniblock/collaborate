## Creating NFT Token for assets

During the creation process, when it contains personal data the owner can flag the datasource. This
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
    * The datasource owner needs to ask a user to consent to the **digital passport NFT** mint by
      calling `POST api/v1/digital-passport`.
    * The user can consent by calling `POST /api/v1/digital-passport/multisig/{contract-id}`. This
      consent triggers the digital-passport NFT mint, representing a user asset.
      ![digital-passport-mint](images/digital-passport-mint.jpg)
* With business dataset:
    * No consent is required, the owner mint directly a **business dataset NFT** for each scope of
      the created datasource.

When a NFT is minted, it will contain metadata containing information about the datasource it is
associated to: the **NFT Catalog**.

```mermaid
classDiagram
    NFT *-- NFT Catalog
    NFT Catalog --* Data source Configuration
```
