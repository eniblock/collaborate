package collaborate.api.tag;

import collaborate.api.tag.config.TagConfig;
import collaborate.api.tag.config.TezosApiGatewayConfClient;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TagService {

  public static final String TOKEN_METADATA_FIELD_NAME = "token_metadata";
  private final TezosApiGatewayConfClient tezosApiGatewayConfClient;
  private final TagStorageClient tagStorageClient;

  public Optional<Integer> findBigmapIdByName(String fieldName, String smartContractAddress) {
    var requestTokenMetadata = new DataFieldsRequest<>(List.of(fieldName));
    var storageResponse = tagStorageClient.getFields(smartContractAddress, requestTokenMetadata);

    return Optional.ofNullable(storageResponse.get(fieldName)).stream()
        .map(field -> Optional.ofNullable(field.get("value")))
        .flatMap(Optional::stream)
        .map(JsonNode::asInt)
        .findFirst();
  }

  public Optional<Integer> findTokenMetadataBigMapId(String smartContractAddress) {
    return findBigmapIdByName(TOKEN_METADATA_FIELD_NAME, smartContractAddress);
  }

  public Optional<String> findTokenMetadataTzktUrl(String smartContractAddress) {
    var tzStatsURL = tezosApiGatewayConfClient.getConfig()
        .findIndexerUrlByName("tzkt")
        .map(url -> StringUtils.replace(url, "api.", ""));

    if (tzStatsURL.isPresent()) {
      var bigmapIdOpt = findTokenMetadataBigMapId(smartContractAddress);
      return bigmapIdOpt.map(bigmapId ->
          UriComponentsBuilder
              .fromHttpUrl(tzStatsURL.get())
              .path("/" + smartContractAddress + "/storage/" + bigmapId)
              .build().toUriString()
      );
    }
    return Optional.empty();
  }

  public TagConfig getConfig() {
    return tezosApiGatewayConfClient.getConfig();
  }

}
