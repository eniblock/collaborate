package collaborate.api.passport.find;

import collaborate.api.passport.model.storage.Multisig;
import collaborate.api.passport.model.storage.PassportMetadata;
import collaborate.api.tag.model.TagEntry;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassportByIdsDTO {

  private Collection<TagEntry<Integer, Multisig>> multisigs;
  private Collection<TagEntry<Integer, PassportMetadata>> passportMetadataByTokenId;

}
