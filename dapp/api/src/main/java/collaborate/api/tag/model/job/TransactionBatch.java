package collaborate.api.tag.model.job;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionBatch<T> {

  private List<Transaction<T>> transactions = new ArrayList<>();
  private String secureKeyName;

}
