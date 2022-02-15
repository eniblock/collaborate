package collaborate.api.datasource.businessdata.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.businessdata.document.model.ScopeAssetDTO;
import collaborate.api.datasource.businessdata.find.FindBusinessDataService;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.gateway.GatewayUrlService;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.http.HttpClientFactory;
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
class ScopeAssetsServiceTest {

  @Mock
  AccessTokenProvider accessTokenProvider;
  @Mock
  ApiProperties apiProperties;
  Clock clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC);
  @Mock
  FindBusinessDataService findBusinessDataService;
  @Mock
  GatewayUrlService gatewayUrlService;
  @Mock
  HttpClientFactory httpClientFactory;
  @Mock
  UserMetadataService userMetadataService;
  @Mock
  CatalogService catalogService;

  @InjectMocks
  ScopeAssetsService scopeAssetsService;

  @BeforeEach
  void setUp() {
    scopeAssetsService = new ScopeAssetsService(
        accessTokenProvider,
        apiProperties,
        clock,
        findBusinessDataService,
        gatewayUrlService,
        httpClientFactory,
        userMetadataService,
        catalogService,
        TestResources.objectMapper);
  }


  @ParameterizedTest
  @MethodSource("getOAuth2_byDatasourceIdParameters")
  void getOAuth2_byDatasourceId(VaultMetadata vaultMetadata,
      OAuth2ClientCredentialsGrant expectedOAuth2) {
    // GIVEN
    String datasourceId = "dsId";
    when(userMetadataService.find(datasourceId, VaultMetadata.class))
        .thenReturn(Optional.ofNullable(vaultMetadata));

    // WHEN
    var oAuth2ResultO = scopeAssetsService.getOAuth2(datasourceId);
    // THEN
    assertThat(oAuth2ResultO).isEqualTo(Optional.ofNullable(expectedOAuth2));
  }

  private static Stream<Arguments> getOAuth2_byDatasourceIdParameters() {
    return Stream.of(
        Arguments.of(
            VaultMetadata.builder()
                .oAuth2(OAuth2ClientCredentialsGrant.builder().build())
                .build(),
            OAuth2ClientCredentialsGrant.builder().build()
        ),
        Arguments.of(
            VaultMetadata.builder().build(),
            null
        ),
        Arguments.of(
            null,
            null
        )
    );
  }

  @ParameterizedTest
  @MethodSource("getJwt_byDatasourceIdAndScopeParameters")
  void getJwt_byDatasourceIdAndScope(VaultMetadata dsVaultMetadata,
      VaultMetadata scopeIdVaultMetadata, AccessTokenResponse expectedResult) {
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
    var oAuth2ResultO = scopeAssetsService.getJwt(datasourceId, scope);
    // THEN
    assertThat(oAuth2ResultO).isEqualTo(Optional.ofNullable(expectedResult));
  }

  private static Stream<Arguments> getJwt_byDatasourceIdAndScopeParameters() {
    return Stream.of(
        Arguments.of(
            VaultMetadata.builder()
                .oAuth2(OAuth2ClientCredentialsGrant.builder().build())
                .build(),
            null,
            AccessTokenResponse.builder().accessToken("jwt").build()
        ),
        Arguments.of(
            null,
            VaultMetadata.builder().jwt("jwt").build(),
            AccessTokenResponse.builder().accessToken("jwt").build()
        ),
        Arguments.of(null, null, null)
    );
  }

  @Test
  void filterByScope() {
    // GIVEN
    var assetListJsonString = TestResources.readContent(
        "/datasource/businessdata/document/asset-list.json");
    var scope = "customers-analytics";
    // WHEN
    var scopeAssetsResult = scopeAssetsService.convertJsonToScopeAssetDTOs(assetListJsonString);
    // THEN
    assertThat(scopeAssetsResult).containsExactlyInAnyOrder(
        ScopeAssetDTO.builder()
            .name("Airport, airline and route data")
            .type("MVP document")
            .synchronizedDate(ZonedDateTime.now(clock))
            .downloadLink(
                URI.create(
                    "https://datasource-dsp-a.fake-datasource.localhost/referentials/airports/download"))
            .build()
    );
  }

}
