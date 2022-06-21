package collaborate.api.tag;

import collaborate.api.tag.model.TagError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class TagClientErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {
    log.error("on {}\n"
            + "response={}",
        response.request(),
        response.body());

    try {
      var tagError = new ObjectMapper().readValue(response.body().toString(), TagError.class);
      return new ResponseStatusException(
          Optional.ofNullable(HttpStatus.resolve(tagError.getStatus()))
              .orElse(HttpStatus.BAD_GATEWAY),
          tagError.getMessage()
      );
    } catch (JsonProcessingException e) {
      return new ResponseStatusException(
          Optional.ofNullable(HttpStatus.resolve(response.status()))
              .orElse(HttpStatus.BAD_GATEWAY),
          response.body().toString()
      );
    }
  }
}