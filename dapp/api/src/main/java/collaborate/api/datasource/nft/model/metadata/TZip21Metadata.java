package collaborate.api.datasource.nft.model.metadata;

import static collaborate.api.datasource.nft.model.metadata.TZip21Metadata.AttributeKeys.ASSET_DATA_CATALOG;
import static collaborate.api.datasource.nft.model.metadata.TZip21Metadata.AttributeKeys.ASSET_ID;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TZip21Metadata {

  @NoArgsConstructor(access = PRIVATE)
  public static class AttributeKeys {

    public static final String ASSET_DATA_CATALOG = "assetDataCatalog";
    public static final String ASSET_ID = "assetId";
  }

  private String name;
  private String symbol;
  private Integer decimals;
  private String description;
  private String version;
  private License license;
  private List<String> authors;
  private String homepage;
  private List<String> interfaces;
  private List<Attribute> attributes;

  @JsonIgnore
  public Optional<Attribute> getAttributeByName(String name) {
    Objects.requireNonNull(name);
    if (attributes == null || attributes.isEmpty()) {
      return Optional.empty();
    }
    return attributes.stream()
        .filter(a -> name.equals(a.getName()))
        .findFirst();
  }

  @JsonIgnore
  public Optional<String> getAssetDataCatalogUri() {
    return getAttributeByName(ASSET_DATA_CATALOG).map(Attribute::getValue);
  }

  @JsonIgnore
  public Optional<String> getAssetId() {
    return getAttributeByName(ASSET_ID).map(Attribute::getValue);
  }

}
