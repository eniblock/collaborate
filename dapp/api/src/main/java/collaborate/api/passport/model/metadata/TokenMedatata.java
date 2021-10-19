package collaborate.api.passport.model.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class TokenMedatata {

  public static final String ASSET_DATA_CATALOG = "assetDataCatalog";
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


  // TODO test me
  public Optional<Attribute> getAttributeByName(String name) {
    Objects.requireNonNull(name);
    if (attributes == null || attributes.size() == 0) {
      return Optional.empty();
    }
    return attributes.stream()
        .filter(a -> name.equals(a.getName()))
        .findFirst();
  }

  // TODO test me
  @JsonIgnore
  public Optional<String> getAssetDataCatalogUri() {
    return getAttributeByName(ASSET_DATA_CATALOG).map(Attribute::getValue);
  }
}
