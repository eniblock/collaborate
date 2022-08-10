package collaborate.api.test;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class TestResources {

  public static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.setSerializationInclusion(Include.NON_NULL);
  }

  /**
   * @param testResourcePath Should begin by a leading slash
   * @return The test resource file content
   */
  public static String readContent(String testResourcePath) {
    try {
      return IOUtils.toString(
          Objects.requireNonNull(
              TestResources.class.getResourceAsStream(testResourcePath)
          ), UTF_8.name()
      );
    } catch (IOException e) {
      throw new IllegalStateException("Can't read resource:" + testResourcePath, e);
    }
  }

  public static <T> T readContent(String testResourcePath, TypeReference<T> type) {
    return readValue(readContent(testResourcePath), type);
  }

  public static <T> T readContent(String testResourcePath, Class<T> clazz) {
    return readValue(readContent(testResourcePath), clazz);
  }

  public static <T> T readValue(String json, TypeReference<T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Can't convert test resource", e);
    }
  }

  public static <T> T readValue(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Can't convert test resource", e);
    }
  }

  public static String asJsonString(Object o) {
    try {
      return objectMapper.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Can't convert o to jsonString", e);
    }
  }

  public static JsonNode readFileAsJsonNode(String testResourcePath) {
    try {
      return objectMapper.readTree(readContent(testResourcePath));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Can't read resource as JSonNode:" + testResourcePath, e);
    }
  }
}
