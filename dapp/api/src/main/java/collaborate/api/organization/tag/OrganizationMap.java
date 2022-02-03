package collaborate.api.organization.tag;

import collaborate.api.tag.model.TagEntry;
import java.util.HashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizationMap extends HashMap<String, TagEntry<String, Organization>[]> {

}
