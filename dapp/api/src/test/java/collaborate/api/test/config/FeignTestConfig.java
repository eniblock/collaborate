package collaborate.api.test.config;

import collaborate.api.businessdata.TAGBusinessDataClient;
import collaborate.api.tag.TezosApiGatewayJobClient;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@ImportAutoConfiguration({RibbonAutoConfiguration.class, FeignAutoConfiguration.class})
@EnableFeignClients(clients = {
    TAGBusinessDataClient.class,
    TezosApiGatewayJobClient.class
})

public class FeignTestConfig {

  @Bean
  public Encoder feignEncoder() {
    var jacksonConverter = new MappingJackson2HttpMessageConverter();
    ObjectFactory<HttpMessageConverters> objectFactory = () ->
        new HttpMessageConverters(jacksonConverter);
    return new SpringEncoder(objectFactory);
  }

  @Bean
  public Decoder feignDecoder() {
    ObjectFactory<HttpMessageConverters> messageConverters = HttpMessageConverters::new;
    return new SpringDecoder(messageConverters);
  }
}
