package collaborate.api.config.api;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@EnableConfigurationProperties(ApiProperties.class)
public class ApiConfig {

  private final ApiProperties apiProperties;

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
  public String contractAddress() {
    return apiProperties.getContractAddress();
  }

  @Bean
  public ModelMapper modelMapper(){
    return new ModelMapper();
  }

  @Bean
  public YamlMapper yamlMapper(){
    return new YamlMapper();
  }

  @Bean
  @Primary
  public ObjectMapper jsonMapper(){
    return new ObjectMapper();
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
}
