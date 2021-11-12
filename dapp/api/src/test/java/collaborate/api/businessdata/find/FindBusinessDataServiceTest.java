package collaborate.api.businessdata.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.passport.model.AccessStatus;
import collaborate.api.passport.model.AssetDataCatalogDTO;
import collaborate.api.passport.model.DatasourceDTO;
import collaborate.api.passport.model.TokenStatus;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindBusinessDataServiceTest {

  @Mock
  FindBusinessDataDAO findBusinessDataDAO;
  @Mock
  OrganizationService organizationService;
  @Mock
  UserMetadataService userMetadataService;

  @InjectMocks
  FindBusinessDataService findBusinessDataService;

  @Test
  void toAssetDetails() {
    // GIVEN
    var organization = OrganizationDTO.builder()
        .legalName("legalName")
        .address("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
        .build();
    when(organizationService.getByWalletAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV"))
        .thenReturn(organization);
    TokenIndex tokenIndex = TokenIndex.builder()
        .tokenId(11)
        .tokenOwnerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
        .assetId("3636ff0b-2295-4750-a6b2-677c680e0bbb:assetId")
        .build();
    when(userMetadataService.find("3636ff0b-2295-4750-a6b2-677c680e0bbb", VaultMetadata.class))
        .thenReturn(Optional.of(VaultMetadata.builder().oAuth2(new OAuth2()).build()));
    // WHEN
    var assetDetailsResult = findBusinessDataService.toAssetDetails(tokenIndex);

    // THEN
    assertThat(assetDetailsResult).isEqualTo(AssetDetailsDTO.builder()
        .assetDataCatalog(
            AssetDataCatalogDTO.builder()
                .datasources(List.of(DatasourceDTO.builder()
                    .id("3636ff0b-2295-4750-a6b2-677c680e0bbb")
                    .assetIdForDatasource("assetId")
                    .build()
                ))
                .build()
        ).assetOwner(organization)
        .assetId("3636ff0b-2295-4750-a6b2-677c680e0bbb:assetId")
        .accessStatus(AccessStatus.GRANTED)
        .tokenStatus(TokenStatus.CREATED)
        .tokenId(11)
        .build());
  }
}
