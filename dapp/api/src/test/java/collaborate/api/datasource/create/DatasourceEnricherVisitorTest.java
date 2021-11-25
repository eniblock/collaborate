package collaborate.api.datasource.create;

import static collaborate.api.test.TestResources.readContent;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatasourceEnricherVisitorTest {

  @Test
  void enrich_shouldResultInExpectedEnrichments() {
    // GIVEN
    var assetListJson = readContent("/datasource/create/business-data-asset-list.response.json");
    var datasourceDTO = WebServerDatasourceDTO.builder()
        .baseUrl("https://datasource-dsp-a.fake-datasource.localhost")
        .resources(emptyList())
        .build();
    // WHEN
    var enrichmentsResult = new DatasourceEnricherVisitor(null)
        .enrich(datasourceDTO, assetListJson);
    // THEN
    assertThat(enrichmentsResult.getDatasource())
        .isEqualTo(
            datasourceDTO.toBuilder()
                .resources(List.of(
                    WebServerResource.builder()
                        .url("/documents/dspA1")
                        .keywords(Set.of("document:dspA1"))
                        .build()
                ))
                .build()
        );
    assertThat(enrichmentsResult.getMetadata()).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("document:dspA1:title")
            .value("Centres ouverts")
            .type("string")
            .build()
    );
  }
}
