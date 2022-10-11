package collaborate.api.datasource.businessdata.create;

import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.TagEntry;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetMetadataMintDTO {

  private String assetId;
  private List<TagEntry<String, Bytes>> metadata;
}
