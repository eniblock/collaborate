package collaborate.api.user.security;

import collaborate.api.services.dto.UserSearchCriteria;
import collaborate.api.services.dto.UserSearchResponseDTO;
import collaborate.api.user.model.UserDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class KeycloakService {

  private final KeycloakUsersClient keycloakClient;

  public List<UserDTO> findAll() {
    UserSearchResponseDTO users = keycloakClient.findByCriteria(new UserSearchCriteria());
    if (users.getContent() == null) {
      return new ArrayList<>();
    } else {
      return users.getContent();
    }
  }

  public Page<UserDTO> findAll(Pageable pageable) {
    UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
    addPaginationInformation(userSearchCriteria, pageable);

    UserSearchResponseDTO responseDto = new UserSearchResponseDTO(null, 0L);
    try {
      responseDto = keycloakClient.findByCriteria(userSearchCriteria);
    } catch (Exception e) {
      log.error("Error retrieving Users from Keycloak");
      log.error(e.getMessage());
    }
    return responseToPage(responseDto, pageable);
  }

  public UserDTO findOneByIdOrElseThrow(UUID id) {
    return keycloakClient.findById(id).get();
  }

  private void addPaginationInformation(UserSearchCriteria userSearchCriteria, Pageable pageable) {
    userSearchCriteria.setPaged(pageable.isPaged());
    if (pageable.isPaged()) {
      userSearchCriteria.setOffset(pageable.getOffset());
      userSearchCriteria.setPageNumber(pageable.getPageNumber());
      userSearchCriteria.setPageSize(pageable.getPageSize());
    }
  }

  private Page<UserDTO> responseToPage(UserSearchResponseDTO response, Pageable pageable) {
    List<UserDTO> content;
    if (CollectionUtils.isEmpty(response.getContent())) {
      content = new ArrayList<>();
    } else {
      content = response.getContent();
    }
    return new PageImpl<>(content, pageable, response.getTotal());
  }

  public Optional<AccessToken> getCurrentAuthToken() {
    Optional<AccessToken> accessTokenOptResult = Optional.empty();
    var rawPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (rawPrincipal instanceof KeycloakPrincipal) {

      KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) rawPrincipal;
      var session = principal.getKeycloakSecurityContext();
      accessTokenOptResult = Optional.of(session.getToken());
    }
    return accessTokenOptResult;
  }

}
