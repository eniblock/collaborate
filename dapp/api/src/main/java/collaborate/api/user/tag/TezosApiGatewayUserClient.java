package collaborate.api.user.tag;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api/user", name = "tag-user-client")
public interface TezosApiGatewayUserClient {

  /**
   * <ul>
   *   <li>Create vault keys for the given {@link UsersDTO#getUserIdList()} list</li>
   *   <li>Activate the accounts on the blockchain network</li>
   * </ul>
   *
   * @return the users tezos address (publicKeys) for each userId
   */
  @PostMapping(value = "create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  ResponseEntity<List<UsersDTO>> create(@RequestBody UsersDTO createUsersDTO);
}
