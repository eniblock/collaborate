package collaborate.api.user;

import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.test.TestResources;

public class UserWalletFeature {

  public static final String tagUserJsonResponse = TestResources
      .readContent("/user/tag/create.user.tag.response.json");

  public static final UserWalletDTO userWallet = UserWalletDTO.builder()
      .userId("asset-owner_._xdev-at-_._1630709967219.test")
      .address("tz1ZukSqsFRoJ9fM6biE4WuCzxj6ZGdCoFXU")
      .build();

  private UserWalletFeature() {
  }
}
