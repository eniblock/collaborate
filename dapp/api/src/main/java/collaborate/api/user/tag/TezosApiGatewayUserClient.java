package collaborate.api.user.tag;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api/user", name = "tag-user-client")
public interface TezosApiGatewayUserClient {

  @Operation(description = "Create vault keys for by user Id, activate the accounts on the blockchain network, and return the Tezos address")
  @PostMapping(value = "create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  ResponseEntity<List<UsersDTO>> create(@RequestBody UsersDTO createUsersDTO);
}
