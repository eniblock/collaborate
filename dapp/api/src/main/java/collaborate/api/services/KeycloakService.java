package collaborate.api.services;

import collaborate.api.restclient.IKeycloakController;
import collaborate.api.services.dto.UserDTO;
import collaborate.api.services.dto.UserSearchCriteria;
import collaborate.api.services.dto.UserSearchResponseDTO;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class KeycloakService {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakService.class);

    private final IKeycloakController keycloakController;
    private final Keycloak keycloak;

    public KeycloakService(IKeycloakController keycloakController, Keycloak keycloak) {
        this.keycloakController = keycloakController;
        this.keycloak = keycloak;
    }

    public List<UserDTO> findAll() {
        UserSearchResponseDTO users = keycloakController.findByCriteria(new UserSearchCriteria());
        if (users.getContent() == null) {
            return new ArrayList<>();
        } else {
            return users.getContent();
        }
    }

    public Page<UserDTO> findAll(Pageable pageable) {
        UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
        addPaginationInformation(userSearchCriteria, pageable);
        
        UserSearchResponseDTO responseDto = new UserSearchResponseDTO(null, 0l);
        try {
            responseDto = keycloakController.findByCriteria(userSearchCriteria);
        } catch (Exception e) {
            LOG.error("Erreur à la récupération des Users de Keycloak");
            LOG.error(e.getMessage());
        }
        return responseToPage(responseDto, pageable);
    }

    public UserDTO findOneByIdOrElseThrow(UUID id) {
        return keycloakController.findById(id).get();
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
