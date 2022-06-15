package collaborate.api.datasource.businessdata.find;

import static collaborate.api.datasource.nft.model.storage.TokenIndexByTokenIdMatcher.matchTokenId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationFeature;
import collaborate.api.organization.OrganizationService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@ExtendWith(MockitoExtension.class)
class FindBusinessDataServiceTest {

  @Mock
  AssetDetailsService assetDetailsService;
  @Mock
  FindBusinessDataDAO findBusinessDataDAO;
  @Mock
  OrganizationService organizationService;
  @InjectMocks
  FindBusinessDataService findBusinessDataService;

  private IndexerTagResponseDTO mockFindByDsp() {
    var orgWallets = List.of(
        OrganizationFeature.dspConsortium1Organization.getAddress(),
        OrganizationFeature.bspConsortium2Organization.getAddress()
    );
    when(organizationService.getAllDspWallets())
        .thenReturn(orgWallets);

    var nftsByDsp = IndexerTagResponseFeature.indexerTagResponse;
    when(findBusinessDataDAO.findNftIndexersByDsps(orgWallets))
        .thenReturn(nftsByDsp);
    return nftsByDsp;
  }

  @Test
  void find_withMaxPageSize_returnsExpectedPageResult() {
    // GIVEN
    int page = 0;
    int size = Integer.MAX_VALUE;
    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    var query = Optional.<String>empty();
    var ownerAddress = Optional.<String>empty();


    IndexerTagResponseDTO nftsByDsp = mockFindByDsp();
    nftsByDsp.streamTokenIndexes()
        .forEach(tokenIndex -> {
          when(assetDetailsService.toAssetDetails(tokenIndex))
              .thenReturn(AssetDetailsDTO.builder()
                  .tokenId(tokenIndex.getTokenId())
                  .build()
              );
        });


    // WHEN

    var assetDetails = findBusinessDataService.find(pageable, query, ownerAddress);

    // THEN
    assertThat(assetDetails).isNotNull();
    assertThat(assetDetails.getTotalElements()).isEqualTo(2);
    assertThat(assetDetails.getTotalPages()).isEqualTo(1);
    assetDetails.getContent().forEach(
        c -> assertThat(List.of(4,19)).contains(c.getTokenId())
    );
  }

  @Test
  void find_withPage0Size1() {
    // GIVEN
    int page = 0;
    int size = 1;
    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    var query = Optional.<String>empty();
    var ownerAddress = Optional.<String>empty();

    mockFindByDsp();
    int expectedTokenId = 4;
    when(assetDetailsService.toAssetDetails(matchTokenId(expectedTokenId)))
        .thenReturn(AssetDetailsDTO.builder()
            .tokenId(expectedTokenId)
            .build()
        );

    // WHEN
    var assetDetails = findBusinessDataService.find(pageable, query, ownerAddress);

    // THEN
    assertThat(assetDetails).isNotNull();
    assertThat(assetDetails.getTotalElements()).isEqualTo(2);
    assertThat(assetDetails.getTotalPages()).isEqualTo(2);
    assertThat(assetDetails.getContent()).hasSize(1);
    assertThat(assetDetails.getContent().get(0).getTokenId()).isEqualTo(expectedTokenId);
  }

  @Test
  void find_withPage1Size1() {
    // GIVEN
    int page = 1;
    int size = 1;
    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    var query = Optional.<String>empty();
    var ownerAddress = Optional.<String>empty();

    mockFindByDsp();
    int expectedTokenId = 19;
    when(assetDetailsService.toAssetDetails(matchTokenId(expectedTokenId)))
        .thenReturn(AssetDetailsDTO.builder()
            .tokenId(expectedTokenId)
            .build()
        );

    // WHEN
    var assetDetails = findBusinessDataService.find(pageable, query, ownerAddress);

    // THEN
    assertThat(assetDetails).isNotNull();
    assertThat(assetDetails.getTotalElements()).isEqualTo(2);
    assertThat(assetDetails.getTotalPages()).isEqualTo(2);
    assertThat(assetDetails.getContent()).hasSize(1);
    assertThat(assetDetails.getContent().get(0).getTokenId()).isEqualTo(expectedTokenId);
  }

  @Test
  void find_withQueryOnTokenId() {
    // GIVEN
    int page = 0;
    int size = 10;
    Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

    int expectedTokenId = 19;
    var query = Optional.of("workshop-analytics");
    var ownerAddress = Optional.<String>empty();

    mockFindByDsp();
    when(assetDetailsService.toAssetDetails(matchTokenId(expectedTokenId)))
        .thenReturn(AssetDetailsDTO.builder()
            .tokenId(expectedTokenId)
            .build()
        );

    // WHEN
    var assetDetails = findBusinessDataService.find(pageable, query, ownerAddress);

    // THEN
    assertThat(assetDetails).isNotNull();
    assertThat(assetDetails.getTotalElements()).isEqualTo(1);
    assertThat(assetDetails.getTotalPages()).isEqualTo(1);
    assertThat(assetDetails.getContent()).hasSize(1);
    assertThat(assetDetails.getContent().get(0).getTokenId()).isEqualTo(expectedTokenId);
  }

}
