package collaborate.api.ipfs;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class IpfsService {

  public static final String IPFS_PROTOCOL_PREFIX = "ipfs://";
  public static final String IPNS_PROTOCOL_PREFIX = "ipns://";
  public static final String IPNS_URI_PREFIX = "/ipns/";

  private final IpfsDAO ipfsDAO;


  public <T> T cat(String address, Class<T> tClass) {
    return ipfsDAO.cat(cleanUri(address), tClass);
  }

  public JsonNode catJson(String address) {
    return cat(address, JsonNode.class);
  }

  public String cleanUri(String address) {
    String cleanedAddress = address;
    if (StringUtils.startsWithIgnoreCase(address, IPFS_PROTOCOL_PREFIX)) {
      cleanedAddress = StringUtils.removeStart(address, IPFS_PROTOCOL_PREFIX);
    } else if (StringUtils.startsWithIgnoreCase(address, IPNS_PROTOCOL_PREFIX)) {
      cleanedAddress = IPNS_URI_PREFIX + StringUtils.removeStart(address, IPNS_PROTOCOL_PREFIX);
    }
    return cleanedAddress;
  }
}
