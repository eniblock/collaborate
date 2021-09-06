package collaborate.api.user.security;

import static java.lang.String.format;

import collaborate.api.user.model.UserDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    return keycloakClient.findById(id)
        .orElseThrow(() -> new IllegalStateException(format("User id=%s not found", id)));
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

}
