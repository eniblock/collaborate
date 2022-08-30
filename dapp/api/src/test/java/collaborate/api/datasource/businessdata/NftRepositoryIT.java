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
  void findAll_shouldFindByCustomAttribute_withMatchingEntries() {
    // GIVEN
    var kpis = TestResources.readContent(
        "/datasource/businessdata/nft.json",
        new TypeReference<List<Nft>>() {
        });
    nftRepository.saveAll(kpis);

    Specification<Nft> specification = new NftSpecification(Map.of(
        "k1", "value1"
    ));
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(1);
  }

  @Test
  void findAll_shouldFindByCustomAttribute_withNoMatchingEntries() {
    // GIVEN
    var kpis = TestResources.readContent(
        "/datasource/businessdata/nft.json",
        new TypeReference<List<Nft>>() {
        });
    nftRepository.saveAll(kpis);

    Specification<Nft> specification = new NftSpecification(Map.of(
        "k1", "v"
    ));
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(0);
  }

  @Test
  void findAll_shouldFindAll_withoutMetadataSpecs() {
    // GIVEN
    var nfts = TestResources.readContent(
        "/datasource/businessdata/nft.json",
        new TypeReference<List<Nft>>() {
        });
    nftRepository.saveAll(nfts);

    var specification = new NftSpecification(null);
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSameSizeAs(nfts);
  }

  @Test
  void findAll_shouldFindByOwner() {
    // GIVEN
    var kpis = TestResources.readContent(
        "/datasource/businessdata/nft.json",
        new TypeReference<List<Nft>>() {
        });
    nftRepository.saveAll(kpis);

    var specification = new NftSpecification(null);
    specification.setOwnerAddress("ownerA");
    // WHEN
    var nftResults = nftRepository.findAll(specification);
    // THEN
    assertThat(nftResults).hasSize(1);
  }
}
