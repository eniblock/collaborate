package collaborate.api.businessdata.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AssetIdValidatorTest {

  @ParameterizedTest
  @MethodSource("isValidParameters")
  void isValid(String assetId, boolean expectedResult) {
    // GIVEN
    var assetIdValidator = new AssetIdValidator();
    // WHEN
    var validResult = assetIdValidator.isValid(assetId, null);
    // THEN
    assertThat(validResult).isEqualTo(expectedResult);
  }

  private static Stream<Arguments> isValidParameters() {
    return Stream.of(
        Arguments.of("id:name", true),
        Arguments.of("id", false),
        Arguments.of(":", false),
        Arguments.of("id:", false),
        Arguments.of(":name", false)
    );
  }
}
