package collaborate.api.businessdata.find;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FindBusinessDataDAO {

  private final TAGBusinessDataClient tagBusinessDataClient;

  public void getAll() {
    
  }

}
