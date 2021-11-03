package collaborate.api.businessdata.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.organization.OrganizationService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindBusinessDataService {

  private final ApiProperties apiProperties;
  private final OrganizationService organizationService;

  public Collection<AssetDetailsDTO> getAll() {
    return null;
  }
}
