package collaborate.api.config;

import collaborate.api.config.properties.ApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
public class ApiConfig {

    @Autowired
    private ApiProperties apiProperties;
}
