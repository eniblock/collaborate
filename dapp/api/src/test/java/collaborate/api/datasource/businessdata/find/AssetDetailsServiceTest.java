package collaborate.api.datasource.businessdata.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.transaction.BusinessDataTransactionService;
import collaborate.api.datasource.kpi.KpiService;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AssetDetailsServiceTest {

  @Mock
  AuthenticationService authenticationService;
  @Mock
  BusinessDataTransactionService businessDataTransactionService;
  @Mock
  KpiService kpiService;
  @Mock
  OrganizationService organizationService;

  @InjectMocks
  AssetDetailsService assetDetailsService;

  @Test
  void toAssetDetails() {
    // GIVEN
    var organization = OrganizationDTO.builder()
        .legalName("legalName")
        .address("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
        .build();
    String datasourceId = "3636ff0b-2295-4750-a6b2-677c680e0bbb";
    int tokenId = 11;

    when(authenticationService.isGranted(datasourceId, tokenId, null))
        .thenReturn(true);
    when(businessDataTransactionService.findTransactionDateByTokenId(nullable(String.class),
        anyString()))
        .thenReturn(Optional.empty());
    when(organizationService.getByWalletAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV"))
        .thenReturn(organization);

    when(kpiService.count(any())).thenReturn(10L);

    TokenIndex tokenIndex = TokenIndex.builder()
        .tokenId(tokenId)
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
                    .id(datasourceId)
                    .assetIdForDatasource("assetId")
                    .ownerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
                    .build()
                ))
                .build()
        ).assetOwner(organization)
        .assetId("3636ff0b-2295-4750-a6b2-677c680e0bbb:assetId")
        .accessStatus(AccessStatus.GRANTED)
        .tokenStatus(TokenStatus.CREATED)
        .tokenId(tokenId)
        .grantedAccess(10L)
        .build());
  }
}
