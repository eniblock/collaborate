package collaborate.api.datasource.create;

import static collaborate.api.test.TestResources.objectMapper;
import static collaborate.api.test.TestResources.readPath;
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
  void build_shouldResultInExpectedEnrichments() {
    // GIVEN
    var assetListJson = readPath("/datasource/create/business-data-asset-list.response.json");
    var datasourceDTO = WebServerDatasourceDTO.builder()
        .baseUrl("https://datasource-dsp-a.fake-datasource.localhost/documents")
        .resources(emptyList())
        .build();
    // WHEN
    var enrichmentsResult =
        new DatasourceEnricherVisitor(null, objectMapper)
            .enrich(datasourceDTO, assetListJson);
    // THEN
    assertThat(enrichmentsResult.getDatasource())
        .isEqualTo(
            datasourceDTO.toBuilder()
                .resources(List.of(
                    WebServerResource.builder()
                        .url("/dspA1")
                        .keywords(Set.of("document:dspA1"))
                        .build()
                ))
                .build()
        );
    assertThat(enrichmentsResult.getMetadata()).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("document:dspA1:title")
            .value("Centres ouverts")
            .build(),
        Metadata.builder()
            .name("document:dspA1:scope")
            .value("referentials")
            .build()
    );
  }
}
