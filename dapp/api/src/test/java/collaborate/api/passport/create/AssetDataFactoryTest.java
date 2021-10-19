package collaborate.api.passport.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.passport.TokenMetadataProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AssetDataFactoryTest {

  Clock clock = Clock
      .fixed(Instant.parse("2021-10-09T16:02:42.12Z"), ZoneId.of("UTC"));

  @Mock
  private DatasourceService datasourceService;
  private DateFormatterFactory dateFormatterFactory = new DateFormatterFactory(clock);
  @Mock
  private TokenMetadataProperties tokenMetadataProperties;

  private AssetDataCatalogFactory assetDataCatalogFactory;

  @BeforeEach
  void setUp() {
    assetDataCatalogFactory = new AssetDataCatalogFactory(
        clock,
        datasourceService,
        dateFormatterFactory,
        tokenMetadataProperties);
  }

  @Test
  void name() {
    // GIVEN
    when(tokenMetadataProperties.getAssetDataCatalogPartitionDatePattern()).thenReturn("yyyyMMdd");
    // WHEN
    var currentPath = assetDataCatalogFactory.buildRelativePathForAssetId("assetId");
    // THEN
    assertThat(currentPath.toString())
        .isEqualTo("DigitalPassport/20211009/assetId_" + clock.millis());
  }
}
