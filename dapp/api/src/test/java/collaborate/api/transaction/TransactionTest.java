package collaborate.api.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;


class TransactionTest {

  @Test
  void isEntryPoint_shouldBeFalse_withNotMatchingEntryPoint() {
    // GIVEN
    var transaction = Transaction.builder()
        .entrypoint("entryPoint")
        .build();
    // WHEN
    var actual = transaction.isEntryPoint("unmatching");
    // THEN
    assertThat(actual).isFalse();
  }

  @Test
  void isEntryPoint_shouldBeFalse_withNullExpected() {
    // GIVEN
    var transaction = Transaction.builder()
        .entrypoint("entryPoint")
        .build();
    // WHEN
    var actual = transaction.isEntryPoint(null);
    // THEN
    assertThat(actual).isFalse();
  }

  @Test
  void isEntryPoint_shouldBeTrue_withMatchingEntryPoint() {
    // GIVEN
    var transaction = Transaction.builder()
        .entrypoint("entryPoint")
        .build();
    // WHEN
    var actual = transaction.isEntryPoint("entryPoint");
    // THEN
    assertThat(actual).isTrue();
  }

  @Test
  void isEntryPoint_shouldBeFalse_withNullEntryPoint() {
    // GIVEN
    var transaction = new Transaction();
    // WHEN
    var actual = transaction.isEntryPoint("entryPoint");
    // THEN
    assertThat(actual).isFalse();
  }

  @Test
  void isSender_shouldBeTrue_withSourceEqualsAddress() {
    // GIVEN
    var senderAddress = "senderAddress";
    var transaction = Transaction.builder().source(senderAddress).build();
    // WHEN
    var actualIsSender = transaction.isSender(senderAddress);
    // THEN
    assertThat(actualIsSender).isTrue();
  }

  @Test
  void isSender_shouldBeFalse_withSourceNotEqualsAddress() {
    // GIVEN
    var senderAddress = "senderAddress";
    var transaction = Transaction.builder().source("another").build();
    // WHEN
    var actualIsSender = transaction.isSender(senderAddress);
    // THEN
    assertThat(actualIsSender).isFalse();
  }

  Transaction buildTransactionWithParameter(String key, String value) {
    var parameters = TestResources.objectMapper.createObjectNode();
    parameters.put(key, value);
    return Transaction.builder().parameters(parameters).build();
  }

  @Test
  void hasParameterValue_shouldBeFalse_whenNullParameters() {
    // GIVEN
    var transaction = new Transaction();
    // WHEN
    var actual = transaction.hasParameterValue("key", "value");
    // THEN
    assertThat(actual).isFalse();
  }

  @Test
  void hasParameterValue_shouldBeFalse_whenParameterKeyExistsWithUnmatchingValue() {
    // GIVEN
    var transaction = buildTransactionWithParameter("key", "value");
    // WHEN
    var actual = transaction.hasParameterValue("key", "unmatching");
    // THEN
    assertThat(actual).isFalse();
  }

  @Test
  void hasParameterValue_shouldBeTrue_whenParameterKeyExistsWithMatchingValue() {
    // GIVEN
    var transaction = buildTransactionWithParameter("key", "value");
    // WHEN
    var actual = transaction.hasParameterValue("key", "value");
    // THEN
    assertThat(actual).isTrue();
  }

  @Test
  void hasParameterValue_shouldBeTrue_whenParameterKeyExistsWithNulValue_and_expectedValueNull() {
    // GIVEN
    var transaction = buildTransactionWithParameter("key", null);
    // WHEN
    var actual = transaction.hasParameterValue("key", null);
    // THEN
    assertThat(actual).isTrue();
  }
}
