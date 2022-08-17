package collaborate.api.organization;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.datasource.kpi.find.FindKpiCustomDAO;
import collaborate.api.organization.model.OrganizationRole;
import collaborate.api.organization.tag.Organization;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import java.util.List;
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
}
