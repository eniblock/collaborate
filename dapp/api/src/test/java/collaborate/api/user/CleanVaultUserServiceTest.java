package collaborate.api.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CleanVaultUserServiceTest {

  private final CleanVaultUserService cleanVaultUserService = new CleanVaultUserService();

  @Test
  void clean_shouldConvertStringAsExpected() {
    // GIVEN
    String email = "asset@owner.net";
    String expected = "asset_._xdev-at_._owner.net";
    // WHEN
    String actual = cleanVaultUserService.cleanUserId(email);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void unclean_shouldConvertStringAsExpected() {
    // GIVEN
    String toUnclean = "asset_._xdev-at_._owner.net";
    String expected = "asset@owner.net";
    // WHEN
    String actual = cleanVaultUserService.uncleanUserId(toUnclean);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void cleanThenUnclean_shouldBeIdentity() {
    // GIVEN
    String email = "asset@owner.net";
    // WHEN
    String actual = cleanVaultUserService.uncleanUserId(cleanVaultUserService.cleanUserId(email));
    // THEN
    assertThat(actual).isEqualTo(email);
  }
}
