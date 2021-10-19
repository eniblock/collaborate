package collaborate.api.tag.model.job;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionBatch<T> {

  private List<Transaction<T>> transactions = new ArrayList<>();
  private String secureKeyName;

}
