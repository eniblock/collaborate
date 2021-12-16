package collaborate.api.datasource.passport.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.organization.OrganizationService;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DigitalPassportDetailsDTOFactory {

  private final ConnectedUserService connectedUserService;
  private final FindPassportDAO findPassportDAO;
  private final OrganizationService organizationService;
  private final UserService userService;
  private final CatalogService catalogService;
  private final NftDatasourceService nftDatasourceService;
  private final ApiProperties apiProperties;


  public List<DigitalPassportDetailsDTO> makeFromFA2(Collection<Integer> tokenIdList) {
    // Get metadata from tokenIdList
    Map<Integer, Object> tokenMetadata = new HashMap<>();
    tokenIdList.stream()
        .forEach(tokenId -> tokenMetadata.put(
            tokenId,
            nftDatasourceService.getTZip21MetadataByTokenId(tokenId,
                apiProperties.getDigitalPassportContractAddress()))
        );

    // Get Alice address
    Map<Integer, String> tokenOwner = new HashMap<>();
    tokenIdList.stream()
        .forEach(tokenId -> tokenOwner.put(
                tokenId,
                null
            )
        );

    // 3) récupérer les @alice dans les indexers (map, avec clef tokenId)
    // 4) récupérer les @dsp dans les indexers (map, avec clef tokenId)

    return new LinkedList<>();
  }

}
