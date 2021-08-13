package collaborate.api.config.api;

import com.fasterxml.jackson.databind.Module;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.web.client.RestTemplate;

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
}
