package collaborate.api.passport.create;

import collaborate.api.tag.model.Bytes;
import collaborate.api.test.TestResources;

public class CreateFeatures {

  private CreateFeatures() {
  }

  public static final String initPassportCreationParamJson = TestResources
      .readContent("/passport/create/sc.entrypoint.initPassportCreation.param.json");
  public static final InitPassportCreationEntryPointParam initPassportCreationEntryPointParam =
      InitPassportCreationEntryPointParam.builder()
          .metadataUri(new Bytes("ipfs://QmXtWB7WJE1NHQDsW7odJM6FmpJT1bawQd6gC6fJav94MP"))
          .nftOwnerAddress("tz1gW6ZzAodKGbUHH1jqtUtryuPmxnhobi3q")
          .assetId("WO0P6Z5QF9FRMXAGC")
          .build();

}
