package collaborate.api.user.tag;

import feign.FeignException.FeignClientException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Service
public class TagUserService {

  private final TezosApiGatewayUserClient tagUserClient;

  /**
   * Vault key that point to wallet (key with coins)
   */
  @Value("${tezos-api-gateway.secureKeyname}")
  private String secureKeyName;

  public List<UsersDTO> create(String userID) {
    UsersDTO usersDTO = UsersDTO.builder()
        .secureKeyName(secureKeyName)
        .userIdList(Set.of(cleanUserId(userID)))
        .build();
    List<UsersDTO> createdUsersResult;

    try {
      ResponseEntity<List<UsersDTO>> response = tagUserClient.create(usersDTO);
      if (response.getStatusCode() != HttpStatus.CREATED) {
        log.error("[TAG] createUsers {}", response);
        throw new HttpClientErrorException(response.getStatusCode());
      }
      createdUsersResult = response.getBody();
    } catch (FeignClientException exception) {
      log.error("[TAG] createUsers", exception);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          String.format("Can't create TAG users, errors:{%s}", exception.getMessage()));
    }

    return createdUsersResult;
  }

  private String cleanUserId(String userID) {
    String cleaned = userID.replace('@', '_');
    log.debug("userId {{}} cleaned as {{}}", userID, cleaned);
    return cleaned;
  }

}
