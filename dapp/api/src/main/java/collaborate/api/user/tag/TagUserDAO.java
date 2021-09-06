package collaborate.api.user.tag;

import static collaborate.api.cache.CacheConfig.CacheNames.ORGANIZATIONS;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.tag.model.user.UsersDTO;
import feign.FeignException.FeignClientException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Service
// TODO Setup a feign error decoder
public class TagUserDAO {

  private final TezosApiGatewayUserClient tagUserClient;

  /**
   * Vault key that point to wallet (key with coins)
   */
  @Value("${tezos-api-gateway.secureKeyname}")
  private String secureKeyName;

  public List<UserWalletDTO> create(String userID) {
    UsersDTO createUsersDTO = UsersDTO.builder()
        .secureKeyName(secureKeyName)
        .userIdList(Set.of(cleanUserId(userID)))
        .build();
    log.debug("[TAG] create({})", createUsersDTO);
    try {
      ResponseEntity<List<UserWalletDTO>> createdUsers = tagUserClient.create(createUsersDTO);
      expectResponseStatusCode(createdUsers, CREATED);
      return createdUsers.getBody();
    } catch (FeignClientException exception) {
      log.error("[TAG] create", exception);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          format("Can't create TAG users, errors:{%s}", exception.getMessage()));
    }
  }

  @Cacheable(value = ORGANIZATIONS)
  public Optional<UserWalletDTO> findOneByAddress(String address) {
    log.debug("[TAG] findOneByPublicKeyHash({})", address);
    Optional<UserWalletDTO> walletOptResult;

    try {
      ResponseEntity<List<UserWalletDTO>> response = tagUserClient
          .findOneUserByAddress(address);

      expectResponseStatusCode(response, OK);
      walletOptResult = uncleanFirstFilteredUser(
          response,
          wallet -> StringUtils.isNotBlank(wallet.getUserId())
      );
    } catch (FeignClientException exception) {
      log.error("[TAG] findOneByPublicKeyHash", exception);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          format("Can't get TAG users, errors:{%s}", exception.getMessage()));
    }
    return walletOptResult;
  }

  @Cacheable(value = ORGANIZATIONS)
  public Optional<UserWalletDTO> findOneByUserId(String userId) {
    log.debug("[TAG] findOneByUserId({})", userId);
    Optional<UserWalletDTO> walletOptResult;

    try {
      ResponseEntity<List<UserWalletDTO>> response = tagUserClient
          .findOneByUserId(cleanUserId(userId));
      expectResponseStatusCode(response, OK);
      walletOptResult = uncleanFirstFilteredUser(
          response,
          wallet -> StringUtils.isNotBlank(wallet.getAddress())
      );
    } catch (FeignClientException exception) {
      log.error("[TAG] findOneByUserId", exception);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          format("Can't get TAG users, errors:{%s}", exception.getMessage()));
    }

    return walletOptResult;
  }

  @Cacheable(value = ORGANIZATIONS)
  public String getOrganizationAccountAddress() {
    return findOneByUserId(secureKeyName)
        .map(UserWalletDTO::getAddress)
        .orElseThrow(() -> new IllegalStateException("No current organization account found"));
  }

  String cleanUserId(String userID) {
    String cleaned = userID.replace("@", "_._xdev-at_._");
    log.debug("userId {{}} cleaned as {{}}", userID, cleaned);
    return cleaned;
  }

  String uncleanUserId(String userID) {
    String uncleaned = userID.replace("_._xdev-at_._", "@");
    log.debug("userId {{}} uncleaned as {{}}", userID, uncleaned);
    return uncleaned;
  }

  void expectResponseStatusCode(ResponseEntity<?> response,
      HttpStatus expectedStatus) {
    if (expectedStatus == null || !expectedStatus.equals(response.getStatusCode())) {
      String errorMessage = format(
          "[TAG] expectedStatus={%s} but actualStatus={%s} for response={%s}",
          expectedStatus,
          response.getStatusCode(),
          response
      );
      log.error(errorMessage);
      throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE, errorMessage);
    }
  }

  Optional<UserWalletDTO> uncleanFirstFilteredUser(ResponseEntity<List<UserWalletDTO>> response,
      Predicate<UserWalletDTO> filter) {
    Optional<UserWalletDTO> walletOptResult = Optional.empty();

    var walletsResponse = response.getBody();
    if (walletsResponse != null) {
      walletOptResult = walletsResponse.stream()
          .filter(filter)
          .findFirst();
      walletOptResult = walletOptResult
          .map(w -> new UserWalletDTO(uncleanUserId(w.getUserId()), w.getAddress()));
    }

    return walletOptResult;
  }

}
