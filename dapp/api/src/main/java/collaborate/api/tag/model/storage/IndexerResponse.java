package collaborate.api.tag.model.storage;

import collaborate.api.tag.model.TezosMap;
import java.util.HashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IndexerResponse extends HashMap<String, TezosMap<String, HashMap<String,String>>>{

}
