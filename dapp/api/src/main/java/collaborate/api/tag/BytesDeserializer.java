package collaborate.api.tag;

import collaborate.api.tag.model.Bytes;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class BytesDeserializer extends StdDeserializer<Bytes> {

  private static final long serialVersionUID = 1514703510863497028L;

  public BytesDeserializer() {
    super(Bytes.class);
  }

  @Override
  public Bytes deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    String inputValue = node.asText();
    return new Bytes(decodeHexString(inputValue));
  }

  public byte[] decodeHexString(String hexString) {
    if (hexString.length() % 2 == 1) {
      throw new IllegalArgumentException(
          "Invalid hexadecimal String supplied.");
    }

    byte[] bytes = new byte[hexString.length() / 2];
    for (int i = 0; i < hexString.length(); i += 2) {
      bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
    }
    return bytes;
  }

  public byte hexToByte(String hexString) {
    int firstDigit = toDigit(hexString.charAt(0));
    int secondDigit = toDigit(hexString.charAt(1));
    return (byte) ((firstDigit << 4) + secondDigit);
  }

  private int toDigit(char hexChar) {
    int digit = Character.digit(hexChar, 16);
    if (digit == -1) {
      throw new IllegalArgumentException("Invalid Hexadecimal Character: " + hexChar);
    }
    return digit;
  }
}