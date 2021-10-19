package collaborate.api.config.api;

import collaborate.api.datasource.DatasourceProperties;
import collaborate.api.ipfs.domain.dto.IpnsFoldersDTO;
import collaborate.api.passport.TokenMetadataProperties;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties({
    ApiProperties.class,
    DatasourceProperties.class,
    TraefikProperties.class,
    TokenMetadataProperties.class
})
public class ApiConfig {

  private final ApiProperties apiProperties;
  private final DatasourceProperties datasourceProperties;
  private final TokenMetadataProperties tokenMetadataProperties;

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  RestTemplateCustomizer hypermediaRestTemplateCustomizer(
      HypermediaRestTemplateConfigurer configurer) {
    return configurer::registerHypermediaTypes;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public Module pageJacksonModule() {
    return new PageJacksonModule();
  }

  @Bean
  public Module sortJacksonModule() {
    return new SortJacksonModule();
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public YamlMapper yamlMapper() {
    return new YamlMapper();
  }

  public String digitalPassportContractAddress() {
    return apiProperties.getDigitalPassportContractAddress();
  }

  @Bean
  public String businessDataContractAddress() {
    return apiProperties.getBusinessDataContractAddress();
  }

  @Bean
  @Primary
  public ObjectMapper jsonMapper() {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setMaxPayloadLength(64000);
    return loggingFilter;
  }

  @Bean
  IpnsFoldersDTO ipnsFolders() {
    var ipnsFolders = new IpnsFoldersDTO();
    ipnsFolders.add(datasourceProperties.getRootFolder());
    ipnsFolders.add(tokenMetadataProperties.getAssetDataCatalogRootFolder());
    ipnsFolders.add(tokenMetadataProperties.getNftMetadataRootFolder());
    return ipnsFolders;
  }
}
