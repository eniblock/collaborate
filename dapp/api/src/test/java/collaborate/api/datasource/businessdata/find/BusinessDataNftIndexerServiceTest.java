package collaborate.api.datasource.businessdata.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationFeature;
import collaborate.api.organization.OrganizationService;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusinessDataNftIndexerServiceTest {

  @Mock
  BusinessDataNftIndexerDAO businessDataNftIndexerDAO;
  @Mock
  OrganizationService organizationService;
  @InjectMocks
  BusinessDataNftIndexerService businessDataNftIndexerService;

  private void mockFindByDsp() {
    var orgWallets = List.of(
        OrganizationFeature.dspConsortium1Organization.getAddress(),
        OrganizationFeature.bspConsortium2Organization.getAddress()
    );
    when(organizationService.getAllDspWallets())
        .thenReturn(orgWallets);
    var nftsByDsp = IndexerTagResponseFeature.indexerTagResponse;
    when(businessDataNftIndexerDAO.findNftIndexersByDsps(orgWallets))
        .thenReturn(nftsByDsp);
  }

  @Test
  void find_withMaxPageSize_returnsExpectedResult() {
    // GIVEN
    var emptyQuery = Optional.<Predicate<TokenIndex>>empty();
    var emptyOwnerAddress = Optional.<String>empty();

    mockFindByDsp();
    // WHEN

    var assetDetails = businessDataNftIndexerService.find(emptyQuery, emptyOwnerAddress);

    // THEN
    assertThat(assetDetails).isNotNull();
    assertThat(assetDetails).hasSize(2);
  }

  @Test
  void find_withQueryOnTokenId() {
    // GIVEN
    int expectedTokenId = 19;
    var query = Optional.of(
        (Predicate<TokenIndex>) t -> t.getAssetId().contains("workshop-analytics"));
    var ownerAddress = Optional.<String>empty();

    mockFindByDsp();

    // WHEN
    var assetDetails = businessDataNftIndexerService.find(query, ownerAddress);

    // THEN
    assertThat(assetDetails).isNotNull();
    assertThat(assetDetails).hasSize(1);
    assertThat(assetDetails.get(0).getTokenId()).isEqualTo(expectedTokenId);
  }

}
