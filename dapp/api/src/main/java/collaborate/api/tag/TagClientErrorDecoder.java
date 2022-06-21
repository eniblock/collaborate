package collaborate.api.tag;

import collaborate.api.tag.model.TagError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * The error decoder usage is defined from properties
 */
@Slf4j
public class TagClientErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {
    log.error("on {}", response.request());

    String body;

    try {
      body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      // Throw a default BadGateway status exception if we can't
      var httpStatus = Optional.ofNullable(HttpStatus.resolve(response.status()))
          .orElse(HttpStatus.BAD_GATEWAY);
      return new ResponseStatusException(
          httpStatus,
          httpStatus.getReasonPhrase()
      );
    }

    try {
      // Throw the exception with TAG error
      log.error("response={}", body);
      var tagError = new ObjectMapper().readValue(body, TagError.class);
      return new ResponseStatusException(
          Optional.ofNullable(HttpStatus.resolve(tagError.getStatus()))
              .orElse(HttpStatus.BAD_GATEWAY),
          tagError.getMessage()
      );
    } catch (JsonProcessingException e) {
      // Throw the exception with the full response body if response was not a TAG error
      var httpStatus = Optional.ofNullable(HttpStatus.resolve(response.status()))
          .orElse(HttpStatus.BAD_GATEWAY);
      return new ResponseStatusException(
          httpStatus,
          body
      );
    }
  }
}