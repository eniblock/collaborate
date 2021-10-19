package collaborate.api.tag.model;

import collaborate.api.tag.BytesDeserializer;
import collaborate.api.tag.BytesSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.nio.charset.StandardCharsets;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

@JsonDeserialize(using = BytesDeserializer.class)
@JsonSerialize(using = BytesSerializer.class)
@Data
@AllArgsConstructor
public class Bytes {

  @NotNull
  private byte[] value;

  public Bytes(@NonNull String str) {
    this.value = str.getBytes(StandardCharsets.UTF_8);
  }

  public String toString() {
    return new String(value, StandardCharsets.UTF_8);
  }
}