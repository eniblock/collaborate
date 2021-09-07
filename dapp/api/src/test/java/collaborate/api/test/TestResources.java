package collaborate.api.test;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class TestResources {

  public static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
  }

  public static String read(String testResourcePath) {
    try {
      return IOUtils.toString(
          Objects.requireNonNull(
              TestResources.class.getResourceAsStream(testResourcePath)
          ), UTF_8.name()
      );
    } catch (IOException e) {
      throw new IllegalStateException("Can't read resource:" + testResourcePath);
    }
  }

  public static <T> T read(String json, TypeReference<T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Can't convert test resource", e);
    }
  }

  public static <T> T read(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Can't convert test resource", e);
    }
  }
}
