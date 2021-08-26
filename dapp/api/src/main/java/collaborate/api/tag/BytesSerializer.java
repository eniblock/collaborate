package collaborate.api.tag;

import collaborate.api.tag.model.Bytes;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * Serialize to the expected smart-contract bytes string
 */
public class BytesSerializer extends StdSerializer<Bytes> {

  public BytesSerializer() {
    this(null);
  }

  public BytesSerializer(Class<Bytes> t) {
    super(t);
  }

  @Override
  public void serialize(
      Bytes value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeString(encodeHexString(value.getValue()));
  }

  private String encodeHexString(byte[] byteArray) {
    var hexStringBuilder = new StringBuilder();
    for (byte b : byteArray) {
      hexStringBuilder.append(byteToHex(b));
    }
    return hexStringBuilder.toString();
  }

  private String byteToHex(byte num) {
    char[] hexDigits = new char[2];
    hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
    hexDigits[1] = Character.forDigit((num & 0xF), 16);
    return new String(hexDigits);
  }
}