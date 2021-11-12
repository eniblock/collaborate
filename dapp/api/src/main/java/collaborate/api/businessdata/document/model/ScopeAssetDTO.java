package collaborate.api.businessdata.document.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import java.net.URI;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class ScopeAssetDTO {

  private String name;
  private String type;
  @ISO8601JsonStringFormat
  private ZonedDateTime synchronizedDate;
  private URI link;
  private URI downloadLink;
}
