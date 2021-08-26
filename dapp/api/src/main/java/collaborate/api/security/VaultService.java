package collaborate.api.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class VaultService {

  private final Map<String, Object> mockVault = new HashMap<>();

  public <T> T get(String key, Class<T> datatype) {
    return (T) this.mockVault.get(key);
  }

  public void put(String key, Object value) {
    this.mockVault.put(key, value);
  }


}

