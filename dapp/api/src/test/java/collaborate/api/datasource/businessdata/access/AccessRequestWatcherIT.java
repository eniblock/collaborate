package collaborate.api.datasource.businessdata.access;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.businessdata.access.AccessRequestWatcherIT.TestConfig;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {AccessRequestWatcher.class, TestConfig.class})

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ActiveProfiles({"default", "test"})
class AccessRequestWatcherIT {

  public static final String PROVIDER_ADDRESS = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV";

  @MockBean
  AccessGrantService accessGrantService;

  @Autowired
  AccessRequestWatcher accessRequestWatcher;

  @TestConfiguration
  public static class TestConfig {

    @Bean
    @Primary
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
