package collaborate.api.user;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import collaborate.api.tag.model.user.TagUserListDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Service
class TagUserDAO {

  private final TagUserClient tagUserClient;
  private final CleanUserService cleanUserService;
  /**
   * Vault key that point to wallet (key with coins)
   */
  @Value("${tezos-api-gateway.secureKeyname}")
  private String secureKeyName;

  public Optional<UserWalletDTO> createActiveUser(String userEmail) {
    UsersDTO createUsersDTO = UsersDTO.builder()
        .secureKeyName(secureKeyName)
        .userIdList(Set.of(cleanUserService.cleanUserId(userEmail)))
        .build();
    log.debug("[TAG] create({})", createUsersDTO);
    try {
      ResponseEntity<List<UserWalletDTO>> createdUsers = tagUserClient.createActiveUser(
          createUsersDTO);
      expectResponseStatusCode(createdUsers, CREATED);
      return Optional.ofNullable(createdUsers.getBody())
          .flatMap(body -> body.stream().findFirst());
    } catch (FeignClientException exception) {
      log.error("[TAG] create", exception);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          format("Can't create TAG users, errors:{%s}", exception.getMessage()));
    }
  }

  public Optional<UserWalletDTO> createUser(String userEmail) {
    TagUserListDTO createUsersDTO = TagUserListDTO.builder()
        .userIdList(Set.of(cleanUserService.cleanUserId(userEmail)))
        .build();
    log.debug("[TAG] create({})", createUsersDTO);
    try {
      ResponseEntity<List<UserWalletDTO>> createdUsers = tagUserClient.createUser(createUsersDTO);
      expectResponseStatusCode(createdUsers, CREATED);
      return Optional.ofNullable(createdUsers.getBody())
          .flatMap(body -> body.stream().findFirst());
    } catch (FeignClientException exception) {
      log.error("[TAG] create", exception);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          format("Can't create TAG users, errors:{%s}", exception.getMessage()));
    }
  }

  public Optional<UserWalletDTO> findOneByWalletAddress(String address) {
    log.debug("[TAG] findOneByPublicKeyHash({})", address);
    Optional<UserWalletDTO> walletOptResult;

    try {
      ResponseEntity<List<UserWalletDTO>> response = tagUserClient
          .findOneUserByAddress(address);

      expectResponseStatusCode(response, OK);
      walletOptResult = updateFirstFilteredUser(
          response,
          wallet -> StringUtils.isNotBlank(wallet.getUserId())
      );
    } catch (FeignClientException exception) {
      log.error("[TAG] findOneByPublicKeyHash", exception);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          format("Can't get TAG users, errors:{%s}", exception.getMessage()));
    }
    return walletOptResult;
  }

  public Optional<UserWalletDTO> findOneByUserEmail(String userEmail) {
    return findOneByUserId(cleanUserService.cleanUserId(userEmail));
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

  Optional<UserWalletDTO> updateFirstFilteredUser(ResponseEntity<List<UserWalletDTO>> response,
      Predicate<UserWalletDTO> filter) {
    Optional<UserWalletDTO> walletOptResult = Optional.empty();

    var walletsResponse = response.getBody();
    if (walletsResponse != null) {
      walletOptResult = walletsResponse.stream()
          .filter(filter)
          .findFirst();
      walletOptResult = walletOptResult
          .map(w -> UserWalletDTO.builder()
              .userId(w.getUserId())
              .address(w.getAddress())
              .email(cleanUserService.uncleanUserId(w.getUserId()))
              .build()
          );
    }

    return walletOptResult;
  }

  public Optional<UserWalletDTO> findOneByUserId(String userid) {
    log.debug("[TAG] findOneByUserId({})", userid);
    Optional<UserWalletDTO> walletOptResult;

    try {
      ResponseEntity<List<UserWalletDTO>> response = tagUserClient
          .findOneByUserId(userid);
      expectResponseStatusCode(response, OK);
      walletOptResult = updateFirstFilteredUser(
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

}
