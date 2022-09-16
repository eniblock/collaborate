package collaborate.api.config.api;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties({
    SmartContractAddressProperties.class,
})
public class SmartContractConfig {

  private final SmartContractAddressProperties smartContractAddressProperties;

  @Bean
  public String digitalPassportContractAddress() {
    return smartContractAddressProperties.getDigitalPassport();
  }

  @Bean
  public String digitalPassportProxyControllerContractAddress() {
    return smartContractAddressProperties.getDigitalPassportProxyTokenController();
  }

  @Bean
  public String serviceDataContractAddress() {
    return smartContractAddressProperties.getServiceData();
  }

  @Bean
  public String serviceDataProxyControllerContractAddress() {
    return smartContractAddressProperties.getServiceDataProxyTokenController();
  }

  @Bean
  public String businessDataContractAddress() {
    return smartContractAddressProperties.getBusinessData();
  }

  @Bean
  public String organizationYellowPageContractAddress() {
    return smartContractAddressProperties.getOrganizationYellowPage();
  }

  @Bean
  public Collection<String> allSmartContracts() {
    return List.of(smartContractAddressProperties.getBusinessData(),
        smartContractAddressProperties.getDigitalPassport(),
        smartContractAddressProperties.getDigitalPassportProxyTokenController(),
        smartContractAddressProperties.getOrganizationYellowPage()
    );
  }
}
