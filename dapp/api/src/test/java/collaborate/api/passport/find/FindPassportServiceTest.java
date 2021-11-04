package collaborate.api.passport.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
import collaborate.api.test.TestResources;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindPassportServiceTest {

  @Mock
  FindPassportDAO findPassportDAO;
  @Mock
  OrganizationService organizationService;

  @InjectMocks
  FindPassportService findPassportService;

  @Test
  void findDspAndPassportIndexerTokenByTokenId_withMissingOrgIndexer() {
    // GIVEN
    var organizationsWallets = List.of(
        "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV",
        "tz1YEAJSJ7j4HNn9adywXxtwttSSHBgyYbyT"
    );

    when(organizationService.getAllDspWallets())
        .thenReturn(organizationsWallets);

    var passportsIndexerByDsp = TestResources.readContent(
        "/passport/find/sc.nft_indexer.response.json",
        PassportsIndexerTagResponseDTO.class
    );
    when(findPassportDAO.findPassportsIndexersByDsps(organizationsWallets))
        .thenReturn(passportsIndexerByDsp);

    // WHEN
    var dspAndPassportResult = findPassportService.findDspAndPassportIndexerTokenByTokenId(1);
    // THEN
    var expectedPassportIndexerToken = TokenIndex.builder()
        .tokenId(1)
        .tokenOwnerAddress("tz1hKFkiMgruSuLbFits2rjBEHuvdfeiUGeK")
        .assetId("1GCDT13X04K151762")
        .build();

    assertThat(dspAndPassportResult)
        .isPresent()
        .hasValue(new SimpleEntry<>("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV",
            expectedPassportIndexerToken));
  }

}
