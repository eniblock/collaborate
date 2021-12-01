package collaborate.api.transaction;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@IdClass(TransactionWatcherStatePK.class)
public class TransactionWatcherState {

  @Id
  private String smartContract;

  @Id
  private String property;

  private String value;
}
