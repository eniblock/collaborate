package collaborate.api.businessdata.access.grant;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.businessdata.access.grant.AccessRequestWatcherIT.TestConfig;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        TestConfig.class,
        AccessRequestWatcher.class})
@ExtendWith(MockitoExtension.class)
class AccessRequestWatcherIT {

  public static final String PROVIDER_ADDRESS = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV";

  @Autowired
  AccessRequestWatcher accessRequestWatcher;

  @TestConfiguration
  public static class TestConfig {

    @Bean
    public OrganizationService organizationService() {
      var organizationService = Mockito.mock(OrganizationService.class);
      when(organizationService.getCurrentOrganization())
          .thenReturn(
              OrganizationDTO.builder()
                  .address(PROVIDER_ADDRESS)
                  .build()
          );
      return organizationService;
    }
  }

  @Test
  void organizationWallet_shouldBeInitializedOnStartup() {
    // GIVEN
    // WHEN
    var currentResult = accessRequestWatcher.organizationWallet;
    // THEN
    assertThat(currentResult).isEqualTo(PROVIDER_ADDRESS);
  }

}
