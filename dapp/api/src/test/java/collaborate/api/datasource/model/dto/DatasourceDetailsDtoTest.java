package collaborate.api.datasource.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.model.dto.web.authentication.transfer.OAuth2;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

class DatasourceDetailsDtoTest {

  @Test
  void serialize_shouldContainPartnerTransferMethodType() throws JsonProcessingException {
    // GIVEN
    var details = DatasourceDetailsDto.builder().partnerTransferMethod(new OAuth2())
        .build();
    // WHEN
    var json = TestResources.objectMapper.writeValueAsString(details);
    // THEN
    assertThat(json).contains("OAuth2");
  }
}
