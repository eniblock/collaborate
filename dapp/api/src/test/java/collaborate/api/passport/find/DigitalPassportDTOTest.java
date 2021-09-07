package collaborate.api.passport.find;

import static collaborate.api.test.TestResources.objectMapper;
import static collaborate.api.test.TestResources.read;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.passport.model.storage.PassportMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class DigitalPassportDTOTest {

  @Test
  void serialized_shouldReturnExpected() throws JsonProcessingException {
    // GIVEN
    ZonedDateTime createdAt = ZonedDateTime.of(
        2021, 9, 7, 12, 7, 37, 56,
        ZoneId.of("UTC")
    );
    var digitalPassportDTO = DigitalPassportDTO.builder()
        .createdAt(createdAt)
        .build();
    // WHEN
    var actualSerialized = objectMapper.writeValueAsString(digitalPassportDTO);
    // THEN
    assertThat(actualSerialized).isEqualTo(read("/passport/find/expected.digitalPassportSerialized.json"));
  }

  @Test
  void deserialize_shouldReturnExpected() throws JsonProcessingException {
    // WHEN
    PassportMetadata passportMetadata = objectMapper.readValue(
        read("/passport/find/sc.tokenMetadata.json"),
        PassportMetadata.class
    );
    // THEN
    assertThat(passportMetadata).isEqualTo(
        PassportMetadata.builder()
            .createdAt(ZonedDateTime.of(
                2021, 9, 7, 10, 5, 27, 0,
                ZoneId.of("UTC"))
            ).datasourceUUID("c66b4e63-be30-41a7-83d1-7f025cef5507")
            .dspAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
            .multisigId(7)
            .vin("NGO5U44G4EUDGVUL9")
            .build()
    );
  }
}
