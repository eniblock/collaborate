package collaborate.api.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@RequiredArgsConstructor
@Slf4j
@Component
public class MultipartFactory {

  private final ObjectMapper objectMapper;

  public MultipartFile create(File file, String formFieldName) throws IOException {
    FileItem fileItem = new DiskFileItem(
        formFieldName,
        Files.probeContentType(file.toPath()),
        false,
        file.getName(),
        (int) file.length(),
        file.getParentFile()
    );
    try (var fileInputStream = new FileInputStream(file)) {
      IOUtils.copy(fileInputStream, fileItem.getOutputStream());
    }
    return new CommonsMultipartFile(fileItem);
  }

  public <T> MultipartFile create(T object, String formFieldName) throws IOException {
    var tmpFileName = RandomStringUtils.randomAlphabetic(32);
    var tmpFile = File.createTempFile(tmpFileName, ".tmp");
    objectMapper.writeValue(tmpFile, object);
    tmpFile.deleteOnExit();

    try {
      var multipart = create(tmpFile, formFieldName);
      Files.delete(tmpFile.toPath());
      return multipart;
    } catch (IOException e) {
      Files.delete(tmpFile.toPath());
      throw e;
    }
  }
}
