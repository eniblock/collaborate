package collaborate.api.user.tag;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.tag.model.user.UsersDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
  ResponseEntity<List<UserWalletDTO>> create(@RequestBody UsersDTO createUsersDTO);

  /**
   * @param publicKeyHash the publicKeyHash for the searched vault user id
   * @return A list containing each provided <code>publicKeyHash</code> where the {@link
   * UserWalletDTO#getUserId()}  is valued if it has been found
   */
  @GetMapping(value = "address", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  ResponseEntity<List<UserWalletDTO>> findOneUserByAddress(
      @RequestParam("userAddressList") String publicKeyHash);

  /**
   * @param userId The vault user id for the searched publicKeyHash
   * @return A list containing each provided <code>userId</code> where the {@link
   * UserWalletDTO#getAddress()} is valued if it has been found
   */
  @GetMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  ResponseEntity<List<UserWalletDTO>> findOneByUserId(@RequestParam("userIdList") String userId);
}
