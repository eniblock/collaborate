package collaborate.api.config;

import feign.Logger.Level;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FeignConfig {

  @Bean
  Level feignLoggerLevel() {
    return Level.BASIC;
  }

  @Bean
  public Encoder multipartFormEncoder() {
    return new SpringFormEncoder(new SpringEncoder(
        () -> new HttpMessageConverters(new RestTemplate().getMessageConverters())));
  }
}
