package collaborate.api.organization;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.datasource.kpi.find.FindKpiCustomDAO;
import collaborate.api.organization.model.OrganizationRole;
import collaborate.api.organization.tag.Organization;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SharedDatabaseTest
@ContextConfiguration(
    classes = {
        PostgresqlSharedTestContainer.Config.class,
        ApiApplication.class,
        FindKpiCustomDAO.class
    }
)
class PendingOrganizationRepositoryIT {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = PostgresqlSharedTestContainer.getInstance();

  @Autowired
  PendingOrganizationRepository pendingOrganizationRepository;

  @Test
  void deleteByAddressIn() {
    // GIVEN
    pendingOrganizationRepository.deleteAll();
    pendingOrganizationRepository.saveAll(List.of(
        Organization.builder()
            .roles(List.of(OrganizationRole.BNO, OrganizationRole.DSP))
            .address("tz1d9eoHqwhXsgndszk7zyqSJwZh9HAFmbxF")
            .legalName("SITA")
            .encryptionKey("encryptionA")
            .build()
    ));
    assertThat(pendingOrganizationRepository.findAll()).hasSize(1);
    // WHEN
    pendingOrganizationRepository.deleteByAddressIn(
        List.of("tz1d9eoHqwhXsgndszk7zyqSJwZh9HAFmbxF"));
    // THEN
    var organizationsResult = pendingOrganizationRepository.findAll();
    assertThat(organizationsResult).isEmpty();
  }

  @Test
  void pendingOrganizationRepository_isInitialized() {
    assertThat(pendingOrganizationRepository).isNotNull();
  }

  @Test
  @Transactional
  void pendingOrganizationRepository_shouldSaveAndFind() {
    // GIVEN
    var expectedOrganization = Organization.builder()
        .roles(List.of(OrganizationRole.BSP, OrganizationRole.BNO))
        .legalName("name")
        .address("address")
        .encryptionKey(
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAppxsXAHOBVuYngB7Y6EBZulPdRuTZWrSsX7Lnid6aYPSSBWJ298RnG25BvO3u8UHczA01X9lVWDoQgelyPY4mb1GKovuPhpDauJQq260/UOXdpioPVYlOBvgWhyvlCCHIEnWfmvE4dowiC5zApI6smX6grOU72dONnvXeRwExeb2OmFdXg1Nn/mFIjLBuuy5Q7isZ7rSCPRVRZHSxG70RKK6x2uxMTAbz02lQz8IxzqNkd7d4FSHKxsTVQsAe3iYtYFLP5S13JmNL5u9FSBBq4dmkqiQTfdZ+KFzTFZeB+9N0SnoP3/fq+oR7cRWCC6lrK0AkNYMkXZHoULRdK9x5wIDAQAB")
        .build();

    // WHEN
    var insertedOrganization = pendingOrganizationRepository.save(expectedOrganization);
    var actualOrganization = pendingOrganizationRepository.findById(
        expectedOrganization.getAddress());
    // THEN
    assertThat(insertedOrganization).isEqualTo(expectedOrganization);
    assertThat(actualOrganization).hasValue(expectedOrganization);
  }
}
