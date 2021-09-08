package collaborate.api.organization;

import static collaborate.api.cache.CacheConfig.CacheNames.ORGANIZATION;
import static java.util.stream.Collectors.toList;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.tag.TezosApiGatewayStorageClient;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrganizationDAO {

  private final ApiProperties apiProperties;
  private final TezosApiGatewayStorageClient tezosApiGatewayStorageClient;
  private final ModelMapper modelMapper;

  public static final String ORGANIZATION_FIELD = "organizations";
  static final DataFieldsRequest<String> GET_ALL_ORGANIZATIONS_REQUEST = new DataFieldsRequest<>(List.of(ORGANIZATION_FIELD));

  public Collection<OrganizationDTO> getAllOrganizations() {

    var organizations = tezosApiGatewayStorageClient.getIndexer(
        apiProperties.getContractAddress(),
        GET_ALL_ORGANIZATIONS_REQUEST
    );

    if (organizations.get(ORGANIZATION_FIELD) == null) {
      return Collections.emptyList();
    } else {
      return organizations.get(ORGANIZATION_FIELD).values().stream()
          .map(o -> modelMapper.map(o, OrganizationDTO.class))
          .collect(toList());
    }
  }

  @Cacheable(value = ORGANIZATION)
  public Optional<OrganizationDTO> getOrganizationByPublicKeyHash(String publicKeyHash) {
    return getAllOrganizations().stream()
        .filter(o -> publicKeyHash.equals(o.getPublicKeyHash()))
        .findFirst();
  }
}

