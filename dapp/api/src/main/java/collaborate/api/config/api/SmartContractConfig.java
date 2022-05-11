package collaborate.api.config.api;

import collaborate.api.datasource.DatasourceProperties;
import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.transaction.TransactionProperties;
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
  public String businessDataContractAddress() {
    return smartContractAddressProperties.getBusinessData();
  }

  @Bean
  public String organizationYellowPageContractAddress() {
    return smartContractAddressProperties.getOrganizationYellowPage();
  }
}