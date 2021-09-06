package collaborate.api.passport.create;

import collaborate.api.test.TestResources;

public class CreateFeatures {

  private CreateFeatures() {
  }

  public static final String initPassportCreationParamJson = TestResources
      .read("/passport/create/sc.entrypoint.initPassportCreation.param.json");
  public static final InitPassportCreationEntryPointParam initPassportCreationEntryPointParam =
      InitPassportCreationEntryPointParam.builder()
          .datasourceUUID("2faca310-f540-4211-9e9f-3acb7269a64f")
          .vin("WO0P6Z5QF9FRMXAGC")
          .dspAddress("edpkv2qoSugVizsZRt9dCb2v4iizRhZEQkw2PF5JyZUgHyE6Bp9Yv2")
          .vehicleOwnerAddress("tz1gW6ZzAodKGbUHH1jqtUtryuPmxnhobi3q")
          .build();

}
