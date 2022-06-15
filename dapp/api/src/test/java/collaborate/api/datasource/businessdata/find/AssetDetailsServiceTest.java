package collaborate.api.datasource.businessdata.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.businessdata.transaction.BusinessDataTransactionService;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AssetDetailsServiceTest {

  @Mock
  BusinessDataTransactionService businessDataTransactionService;
  @Mock
  OrganizationService organizationService;
  @Mock
  UserMetadataService userMetadataService;

  @InjectMocks
  AssetDetailsService assetDetailsService;

  @Test
  void toAssetDetails() {
    // GIVEN
    var organization = OrganizationDTO.builder()
        .legalName("legalName")
        .address("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
        .build();

    when(businessDataTransactionService.findTransactionDateByTokenId(nullable(String.class), anyString()))
        .thenReturn(Optional.empty());
    when(organizationService.getByWalletAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV"))
        .thenReturn(organization);
    when(userMetadataService.getOwnerOAuth2("3636ff0b-2295-4750-a6b2-677c680e0bbb"))
        .thenReturn(Optional.of(new OAuth2ClientCredentialsGrant()));

    TokenIndex tokenIndex = TokenIndex.builder()
        .tokenId(11)
        .tokenOwnerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
        .assetId("3636ff0b-2295-4750-a6b2-677c680e0bbb:assetId")
        .build();
    // WHEN
    var assetDetailsResult = assetDetailsService.toAssetDetails(tokenIndex);

    // THEN
    assertThat(assetDetailsResult).isEqualTo(AssetDetailsDTO.builder()
        .assetDataCatalog(
            AssetDataCatalogDTO.builder()
                .datasources(List.of(AssetDetailsDatasourceDTO.builder()
                    .id("3636ff0b-2295-4750-a6b2-677c680e0bbb")
                    .assetIdForDatasource("assetId")
                    .ownerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
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
