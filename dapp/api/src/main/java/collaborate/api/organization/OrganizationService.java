package collaborate.api.organization;

import static java.util.stream.Collectors.toCollection;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.organization.tag.SelectOrganizations;
import collaborate.api.organization.tag.TezosApiGatewayOrganizationClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

  private final ApiProperties apiProperties;
  private final TezosApiGatewayOrganizationClient tagOrganizationClient;
  private final ModelMapper modelMapper;

  public Collection<OrganizationDTO> getAllOrganizations() {
    var organizations = tagOrganizationClient
        .getOrganizations(apiProperties.getContractAddress(),
            SelectOrganizations.getInstance());

    if (organizations.getOrganizationByPublicKeyHashes() == null) {
      return Collections.emptyList();
    } else {
      return organizations.getOrganizationByPublicKeyHashes().values().stream()
          .map(o -> modelMapper.map(o, OrganizationDTO.class))
          .collect(toCollection(ArrayList::new));
    }
  }
}

