package collaborate.api.datasource.nft;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.ipfs.IpfsService;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

  public static final String TZIP21_URL = "ipfs://QmaeQiKVNdo2B1LMNskyjoYC9rnQRMZghrRKn5TRVK1woJ";
  @Mock
  private IpfsService ipfsService;
  @Mock
  private TokenDAO tokenDAO;
  @InjectMocks
  private TokenService tokenService;
  private final String contract = "smart-contract-address";
  private final Integer tokenId = 1;

  private final List<TagEntry<String, Bytes>> tokenResponse = List.of(
      TagEntry.<String, Bytes>builder()
          .key("")
          .value(new Bytes(TZIP21_URL))
          .build());

  @Test
  void getOnChainMetadataByTokenId() {
    // GIVEN
    when(tokenDAO.getMetadataByTokenId(contract, tokenId)).thenReturn(Optional.of(tokenResponse));
    // WHEN
    var r = tokenService.getOnChainMetadataByTokenId(contract, tokenId);
    // THEN
    assertThat(r).hasValue(
        Map.of("", TZIP21_URL));
  }

  @Test
  void getOffChainMetadataByTokenId() {
    // GIVEN
    when(tokenDAO.getMetadataByTokenId(contract, tokenId)).thenReturn(Optional.of(tokenResponse));
    when(ipfsService.catJson(TZIP21_URL))
        .thenReturn(readContent("/datasource/nft/t-zip-21.json", JsonNode.class));
    // WHEN
    var tZip21OptResult = tokenService.getOffChainMetadataByTokenId(contract, tokenId);
    // THEN
    assertThat(tZip21OptResult).isPresent();
    assertThat(tZip21OptResult.get().get("name")).isEqualTo(new TextNode("CatalogBusinessData"));
  }
}
