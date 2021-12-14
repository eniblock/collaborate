package collaborate.api.datasource.passport.create;

import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.proxytokencontroller.MultisigBuildCallParamMint;
import collaborate.api.tag.model.proxytokencontroller.MultisigBuildParam;
import collaborate.api.tag.model.proxytokencontroller.MultisigMetadata;
import collaborate.api.tag.model.proxytokencontroller.dto.MultisigBuildCallParamMintDetails;
import collaborate.api.tag.model.proxytokencontroller.dto.MultisigBuildCallParamMintDetailsMint;
import collaborate.api.tag.model.proxytokencontroller.dto.MultisigBuildCallParamMintDetailsMintParams;
import collaborate.api.test.TestResources;
import java.util.List;

public class CreateFeatures {

  private CreateFeatures() {
  }

  public static final String initPassportCreationParamJson = TestResources
      .readContent("/datasource/passport/create/sc.entrypoint.initPassportCreation.param.json");

  public static final MultisigBuildParam initPassportCreationEntryPointParam =
      MultisigBuildParam.builder()
          .buildAndSign(true)
          .multisigId(30)
          .signers(List.of("tz1U5jsPDiRPDM1wWqrWXQW3zkaGrbpskJVc"))
          .callParams(
              MultisigBuildCallParamMint.builder()
                  .targetAddress("KT1Wss6SAnRzcKPmx1Ffq3YJNcjvt8f1Wi2s")
                  .parameters(
                      MultisigBuildCallParamMintDetails.builder()
                          .mint(
                              MultisigBuildCallParamMintDetailsMint.builder()
                                  .operator("tz1WpmFuSZfuNS7XDKwDZxX3QhSNUneTkiTv")
                                  .mintParams(
                                      MultisigBuildCallParamMintDetailsMintParams.builder()
                                          .amount(1)
                                          .address("tz1gW6ZzAodKGbUHH1jqtUtryuPmxnhobi3q")
                                          .metadata(List.of(MultisigMetadata.builder()
                                              .key("")
                                              .value(new Bytes("ipfs://QmXtWB7WJE1NHQDsW7odJM6FmpJT1bawQd6gC6fJav94MP"))
                                              .build()))
                                          .build()
                                  )
                                  .build()
                          )
                          .build()
                  )
                  .build()
          )
          .build();

}
