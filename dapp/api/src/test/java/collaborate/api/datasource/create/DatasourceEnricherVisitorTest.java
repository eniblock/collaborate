package collaborate.api.datasource.create;

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
        .baseUrl("https://dsp.com")
        .resources(emptyList())
        .build();
    // WHEN
    var enrichmentsResult = new DatasourceEnricherVisitor(null)
        .build(datasourceDTO, assetListJson);
    // THEN
    assertThat(enrichmentsResult.getDatasource())
        .isEqualTo(
            datasourceDTO.toBuilder()
                .resources(List.of(
                    WebServerResource.builder()
                        .url("/pcc/centers")
                        .keywords(Set.of("document:centersId"))
                        .build()
                ))
                .build()
        );
    assertThat(enrichmentsResult.getMetadata()).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("document:centersId:title")
            .value("Center title")
            .build(),
        Metadata.builder()
            .name("document:centersId:scope")
            .value("center-scope")
            .build()
    );
  }
}
