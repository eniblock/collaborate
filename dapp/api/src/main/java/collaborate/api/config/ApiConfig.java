package collaborate.api.config;

import collaborate.api.config.properties.ApiProperties;
import com.fasterxml.jackson.databind.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
public class ApiConfig {

    @Autowired
    private ApiProperties apiProperties;

    @Bean
    RestTemplateCustomizer hypermediaRestTemplateCustomizer(HypermediaRestTemplateConfigurer configurer) {
        return restTemplate -> {
            configurer.registerHypermediaTypes(restTemplate);
        };
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
}
