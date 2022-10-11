package collaborate.api.datasource.businessdata;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.datasource.model.Nft;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.TestResources;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractIntegerAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SharedDatabaseTest
@ContextConfiguration(
    classes = {
        PostgresqlSharedTestContainer.Config.class,
        ApiApplication.class
    }
)
class NftRepositoryIT {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = PostgresqlSharedTestContainer.getInstance();

  @Autowired
  NftRepository nftRepository;

  @Test
  void isInitialized() {
    assertThat(nftRepository).isNotNull();
  }

  @Test
  void findAll_shouldFindAll_withNullMetadataSpec() {
    // GIVEN
    List<Nft> nfts = populateNftRepository();

    var specification = new NftSpecification(null);
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSameSizeAs(nfts);
  }


  @Test
  void findAll_shouldNotFindByMetadata_withNoNftMatchingSpec() {
    // GIVEN
    populateNftRepository();

    Specification<Nft> specification = new NftSpecification(Map.of(
        "k1", "v"
    ));
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(0);
  }

  @Test
  void findAll_shouldFindByMetadata_withSingleMatchingSpec() {
    // GIVEN
    populateNftRepository();

    Specification<Nft> specification = new NftSpecification(Map.of(
        "k1", "value1"
    ));
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(1);
    assertThatFirstNftId(nftResults).isEqualTo(1);
  }

  @Test
  void findAll_shouldFindByMetadata_withMultipleMatchingSpec() {
    // GIVEN
    populateNftRepository();

    Specification<Nft> specification = new NftSpecification(Map.of(
        "k1", "value1",
        "k2", "value2"
    ));
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(1);
    assertThatFirstNftId(nftResults).isEqualTo(1);
  }

  @Test
  void findAll_shouldNotFind_withPartialMatchingSpec() {
    // GIVEN
    populateNftRepository();

    Specification<Nft> specification = new NftSpecification(Map.of(
        "k1", "value1",
        "k2", "notMatchingValue"
    ));
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(0);
  }

  @Test
  void findAll_shouldFindByOwner_withEqOwnerAsSpecAttribute() {
    // GIVEN
    populateNftRepository();

    var specification = new NftSpecification(null);
    specification.setEqOwnerAddress("ownerA");
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(1);
    assertThatFirstNftId(nftResults).isEqualTo(1);
  }

  @Test
  void findAll_shouldExcludeNotEqOwner_withNoMetadataSpec() {
    // GIVEN
    populateNftRepository();

    var specification = new NftSpecification(null);
    specification.setNotEqOwnerAddress("ownerA");
    // WHEN
    var nftIdsResults = nftRepository.findAll(specification)
        .stream()
        .map(Nft::getNftId)
        .collect(Collectors.toList());
    // THEN
    assertThat(nftIdsResults).hasSize(2);
    assertThat(nftIdsResults).containsExactlyInAnyOrder(2, 3);
  }

  @Test
  void findAll_shouldExcludeNotEqOwnerAndIncludeEqOwnerAddress_withNoMetadataSpec() {
    // GIVEN
    populateNftRepository();

    var specification = new NftSpecification(null);
    specification.setEqOwnerAddress("ownerA");
    specification.setNotEqOwnerAddress("ownerB");
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(1);
    assertThatFirstNftId(nftResults).isEqualTo(1);
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
  private static AbstractIntegerAssert<?> assertThatFirstNftId(List<Nft> nftResults) {
    return assertThat(nftResults.get(0).getNftId());
  }
}
