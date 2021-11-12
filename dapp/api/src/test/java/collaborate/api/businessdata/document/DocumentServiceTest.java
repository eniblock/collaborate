package collaborate.api.businessdata.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.businessdata.document.model.ScopeAssetDTO;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.AccessTokenProvider;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.gateway.GatewayUrlService;
import collaborate.api.nft.find.TokenMetadataService;
import collaborate.api.test.TestResources;
import collaborate.api.user.metadata.UserMetadataService;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock
  AccessTokenProvider accessTokenProvider;
  @Mock
  ApiProperties apiProperties;
  Clock clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC);
  @Mock
  GatewayUrlService gatewayUrlService;
  @Mock
  UserMetadataService userMetadataService;
  @Mock
  TokenMetadataService tokenMetadataService;

  @InjectMocks
  DocumentService documentService;

  @BeforeEach
  void setUp() {
    documentService = new DocumentService(
        accessTokenProvider,
        apiProperties,
        clock,
        gatewayUrlService,
        userMetadataService,
        tokenMetadataService);
  }


  @ParameterizedTest
  @MethodSource("getOAuth2_byDatasourceIdParameters")
  void getOAuth2_byDatasourceId(VaultMetadata vaultMetadata, Optional<OAuth2> expectedOAuth2Opt) {
    // GIVEN
    String datasourceId = "dsId";
    when(userMetadataService.find(datasourceId, VaultMetadata.class))
        .thenReturn(Optional.ofNullable(vaultMetadata));

    // WHEN
    var oAuth2ResultO = documentService.getOAuth2(datasourceId);
    // THEN
    assertThat(oAuth2ResultO).isEqualTo(expectedOAuth2Opt);
  }

  private static Stream<Arguments> getOAuth2_byDatasourceIdParameters() {
    return Stream.of(
        Arguments.of(
            VaultMetadata.builder()
                .oAuth2(OAuth2.builder().build())
                .build(),
            Optional.of(OAuth2.builder().build())
        ),
        Arguments.of(
            VaultMetadata.builder().build(),
            Optional.empty()
        ),
        Arguments.of(
            null,
            Optional.empty()
        )
    );
  }

  @ParameterizedTest
  @MethodSource("getJwt_byDatasourceIdAndScopeParameters")
  void getJwt_byDatasourceIdAndScope(VaultMetadata dsVaultMetadata,
      VaultMetadata scopeIdVaultMetadata, Optional<AccessTokenResponse> expectedResult) {
    // GIVEN
    String datasourceId = "dsId";
    String scope = "scope:scopeA";
    when(userMetadataService.find(datasourceId, VaultMetadata.class))
        .thenReturn(Optional.ofNullable(dsVaultMetadata));

    if (dsVaultMetadata != null) {
      when(accessTokenProvider.get(dsVaultMetadata.getOAuth2(), Optional.of("scopeA")))
          .thenReturn(AccessTokenResponse.builder().accessToken("jwt").build());
    } else {
      when(userMetadataService.find(datasourceId + ":scopeA", VaultMetadata.class))
          .thenReturn(Optional.ofNullable(scopeIdVaultMetadata));
    }

    // WHEN
    var oAuth2ResultO = documentService.getJwt(datasourceId, scope);
    // THEN
    assertThat(oAuth2ResultO).isEqualTo(expectedResult);
  }

  private static Stream<Arguments> getJwt_byDatasourceIdAndScopeParameters() {
    return Stream.of(
        Arguments.of(
            VaultMetadata.builder()
                .oAuth2(OAuth2.builder().build())
                .build(),
            null,
            Optional.of(AccessTokenResponse.builder().accessToken("jwt").build())
        ),
        Arguments.of(
            null,
            VaultMetadata.builder().jwt("jwt").build(),
            Optional.of(AccessTokenResponse.builder().accessToken("jwt").build())
        ),
        Arguments.of(
            null,
            null,
            Optional.empty()
        )
    );
  }

  @Test
  void filterByScope() {
    // GIVEN
    var assetListJsonString = TestResources.readContent("/businessdata/document/asset-list.json");
    var scope = "customers-analytics";
    // WHEN
    var scopeAssetsResult = documentService.filterByScope(assetListJsonString, scope);
    // THEN
    assertThat(scopeAssetsResult).containsExactlyInAnyOrder(
        ScopeAssetDTO.builder()
            .name("Nombre de metrics saisis en atelier (par mois)")
            .type("")
            .synchronizedDate(ZonedDateTime.now(clock))
            .link(URI.create("http://fake-datasource-api-dsp-a:3000/documents/dspA2"))
            .downloadLink(
                URI.create("http://fake-datasource-api-dsp-a:3000/documents/dspA2/download"))
            .build(),
        ScopeAssetDTO.builder()
            .name("Contactabilité des clients (à date)")
            .type("")
            .synchronizedDate(ZonedDateTime.now(clock))
            .link(URI.create("http://fake-datasource-api-dsp-a:3000/documents/dspA6"))
            .downloadLink(
                URI.create("http://fake-datasource-api-dsp-a:3000/documents/dspA6/download"))
            .build()
    );
  }
}
