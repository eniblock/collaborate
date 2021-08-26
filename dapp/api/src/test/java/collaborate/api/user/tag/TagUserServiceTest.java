package collaborate.api.user.tag;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagUserServiceTest {

  @InjectMocks
  TagUserService tagUserService;

  @Test
  void clean_shouldConvertStringAsExpected() {
    // GIVEN
    String email = "asset@owner.net";
    String expected = "asset_._xdev-at_._owner.net";
    // WHEN
    String actual = tagUserService.cleanUserId(email);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void unclean_shouldConvertStringAsExpected() {
    // GIVEN
    String toUnclean = "asset_._xdev-at_._owner.net";
    String expected = "asset@owner.net";
    // WHEN
    String actual = tagUserService.uncleanUserId(toUnclean);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void cleanThenUnclean_shouldBeIdentity() {
    // GIVEN
    String email = "asset@owner.net";
    // WHEN
    String actual = tagUserService.uncleanUserId(tagUserService.cleanUserId(email));
    // THEN
    assertThat(actual).isEqualTo(email);
  }
}
