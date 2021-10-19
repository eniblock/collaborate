package collaborate.api.organization.tag;

import collaborate.api.tag.model.TezosMap;
import java.util.HashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrganizationMap extends HashMap<String, TezosMap<String, Organization>>{

}
