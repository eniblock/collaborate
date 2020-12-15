package collaborate.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import collaborate.api.errors.UserIdNotFoundException;
import collaborate.api.restclient.IKeycloakController;
import collaborate.api.services.dto.UserDTO;
import collaborate.api.services.dto.UserSearchCriteria;
import collaborate.api.services.dto.UserSearchResponseDTO;

import org.keycloak.admin.client.Keycloak;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class KeycloakService {

    private final IKeycloakController keycloakController;
    private final Keycloak keycloak;

    public KeycloakService(IKeycloakController keycloakController, Keycloak keycloak) {
        this.keycloakController = keycloakController;
        this.keycloak = keycloak;
    }

    public List<UserDTO> findAll() {
        UserSearchResponseDTO users = keycloakController.findByCriteria(getBearerToken(), new UserSearchCriteria());
        if (users.getContent() == null) {
            return new ArrayList<>();
        } else {
            return users.getContent();
        }
    }

    public Page<UserDTO> findAll(Pageable pageable) {
        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        addPaginationInformation(userSearchCriteria, pageable);
        addSortInformation(userSearchCriteria, pageable.getSort());

        UserSearchResponseDTO responseDto = keycloakController.findByCriteria(getBearerToken(), userSearchCriteria);
        return responseToPage(responseDto, pageable);
    }

    public UserDTO findOneByIdOrElseThrow(UUID id) throws UserIdNotFoundException {
        return keycloakController.findById(getBearerToken(), id).orElseThrow(() -> new UserIdNotFoundException(id));
    }

    private String getBearerToken() {
        return "Bearer " + keycloak.tokenManager().getAccessTokenString();
    }

    private void addPaginationInformation(UserSearchCriteria userSearchCriteria, Pageable pageable) {
        userSearchCriteria.setPaged(pageable.isPaged());
        if (pageable.isPaged()) {
            userSearchCriteria.setOffset(pageable.getOffset());
            userSearchCriteria.setPageNumber(pageable.getPageNumber());
            userSearchCriteria.setPageSize(pageable.getPageSize());
        }
    }

    private void addSortInformation(UserSearchCriteria userSearchCriteria, Sort sort) {
        userSearchCriteria.setSorted(sort.isSorted());
        if (sort.isSorted()) {
            List<String> sorts = new ArrayList<>();
            userSearchCriteria.setSort(sorts);
            sort.forEach(order -> {
                StringBuilder sb = new StringBuilder(order.getProperty());
                if (order.isAscending()) {
                   sb.append(",asc");
                } else {
                    sb.append(",desc");
                }
                sorts.add(sb.toString());
            });
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
