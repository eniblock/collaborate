package collaborate.api.datasource.businessdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Pageable.unpaged;

import collaborate.api.ApiApplication;
import collaborate.api.datasource.businessdata.find.BusinessDataNftIndexerService;
import collaborate.api.datasource.model.Nft;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.TestResources;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.AbstractIntegerAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SharedDatabaseTest
@ContextConfiguration(
    classes = {
        PostgresqlSharedTestContainer.Config.class,
        ApiApplication.class,
        NftService.class
    }
)
class NftServiceIT {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = PostgresqlSharedTestContainer.getInstance();

  @Autowired
  NftRepository nftRepository;
  @Autowired
  NftService nftService;
  @MockBean
  BusinessDataNftIndexerService businessDataNftIndexerService;
  @MockBean
  OrganizationService organizationService;
  @MockBean
  ObjectMapper objectMapper;

  @Test
  void isInitialized() {
    assertThat(nftRepository).isNotNull();
    assertThat(nftService).isNotNull();
  }

  @Test
  void findMarketPlaceByFilters_shouldFindAll_withNullFilters() {
    // GIVEN
    List<Nft> nfts = populateNftRepository();
    // WHEN
    var nftResults = nftService.findMarketPlaceByFilters(null, unpaged());
    // THEN
    assertThat(nftResults).hasSameSizeAs(nfts);
  }


  @Test
  void findMarketPlaceByFilters_shouldNotFind_withNoNftMatchingSpec() {
    // GIVEN
    populateNftRepository();

    var filters = Map.of("k1", "v");
    // WHEN
    var nftResults = nftService.findMarketPlaceByFilters(filters, unpaged());
    // THEN
    assertThat(nftResults).hasSize(0);
  }

  @Test
  void findMarketPlaceByFilters_shouldFindByOwner_withOwnerFilterMatchingExistingOrganizationLabel() {
    // GIVEN
    populateNftRepository();

    String organizationLegalName = "legalName";
    var filters = Map.of("owner", organizationLegalName);
    when(organizationService.findByLegalNameIgnoreCase(organizationLegalName))
        .thenReturn(Optional.of(OrganizationDTO.builder().address("ownerA").build()));
    // WHEN
    var nftResults = nftService.findMarketPlaceByFilters(filters, unpaged());
    // THEN
    assertThat(nftResults).hasSize(1);
    assertThatFirstNftId(nftResults).isEqualTo(1);
  }

  @Test
  void findMarketPlaceByFilters_shouldExcludeCurrentOrg_withNoMetadataSpec() {
    // GIVEN
    populateNftRepository();
    when(organizationService.getCurrentAddress())
        .thenReturn("ownerA");
    // WHEN
    var nftResults = nftService.findMarketPlaceByFilters(null, unpaged());
    // THEN
    assertThat(nftResults).hasSize(2);
    assertThatFirstNftId(nftResults).isEqualTo(2);
  }

  @Test
  void findMarketPlaceByFilters_shouldFind_withMatchingScopeMetadataSpec() {
    // GIVEN
    populateNftRepository();
    // WHEN
    var nftResults = nftService.findMarketPlaceByFilters(
        Map.of("scope", "expected-scope-A"),
        unpaged());
    // THEN
    assertThat(nftResults).hasSize(1);
    assertThatFirstNftId(nftResults).isEqualTo(2);
  }


  private List<Nft> populateNftRepository() {
    var kpis = TestResources.readContent(
        "/datasource/businessdata/nft.json",
        new TypeReference<List<Nft>>() {
        });
    nftRepository.saveAll(kpis);
    return kpis;
  }

  @NotNull
  private static AbstractIntegerAssert<?> assertThatFirstNftId(Page<Nft> nftResults) {
    return assertThat(nftResults.getContent().get(0).getNftId());
  }
}
