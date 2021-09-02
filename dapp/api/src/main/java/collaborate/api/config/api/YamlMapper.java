package collaborate.api.config.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlMapper extends ObjectMapper {

  public YamlMapper() {
    super(new YAMLFactory());
  }
}
