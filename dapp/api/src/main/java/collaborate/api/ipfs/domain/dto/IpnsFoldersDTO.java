package collaborate.api.ipfs.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class IpnsFoldersDTO {

  private Set<String> ipnsFolders = ConcurrentHashMap.newKeySet();

  @JsonIgnore
  public boolean add(String absolutePath) {
    return ipnsFolders.add(absolutePath);
  }
}
