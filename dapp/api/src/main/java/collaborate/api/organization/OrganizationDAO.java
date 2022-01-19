package collaborate.api.organization;

import static collaborate.api.cache.CacheConfig.CacheNames.ORGANIZATION;
import static java.util.stream.Collectors.toList;

import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.organization.tag.TezosApiGatewayStorageClient;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import java.util.Arrays;
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

  private final TezosApiGatewayStorageClient tezosApiGatewayStorageClient;
  private final ModelMapper modelMapper;

  public static final String ORGANIZATION_FIELD = "organizations";
  static final DataFieldsRequest<String> GET_ALL_ORGANIZATIONS_REQUEST = new DataFieldsRequest<>(
      List.of(ORGANIZATION_FIELD));

  public Collection<OrganizationDTO> getAllOrganizations(String smartContractAddress) {
    var organizations = tezosApiGatewayStorageClient.getOrganizations(
        smartContractAddress,
        GET_ALL_ORGANIZATIONS_REQUEST
    );

    if (organizations.get(ORGANIZATION_FIELD) == null) {
      return Collections.emptyList();
    } else {
      return Arrays.stream(organizations.get(ORGANIZATION_FIELD))
          .map(TagEntry::getValue)
          .map(organization -> modelMapper.map(organization, OrganizationDTO.class))
          .collect(toList());
    }
  }

  @Cacheable(value = ORGANIZATION)
  public Optional<OrganizationDTO> findOrganizationByPublicKeyHash(String address,
      String smartContractAddress) {
    return getAllOrganizations(smartContractAddress).stream()
        .filter(o -> address.equals(o.getAddress()))
        .findFirst();
  }
}
