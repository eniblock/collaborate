package collaborate.api.tag.model.storage;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataFieldsRequest<T> {

  List<T> dataFields = new ArrayList<>();

}

