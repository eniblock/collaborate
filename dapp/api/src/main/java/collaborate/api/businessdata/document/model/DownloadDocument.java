package collaborate.api.businessdata.document.model;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadDocument {

  private String fileName;
  private File file;
}
