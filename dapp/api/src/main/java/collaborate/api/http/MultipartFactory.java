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
    IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
    return new CommonsMultipartFile(fileItem);
  }

  public <T> MultipartFile create(T object, String formFieldName) throws IOException {
    var tmpFileName = RandomStringUtils.randomAlphabetic(32);
    var tmpFile = File.createTempFile(tmpFileName, ".tmp");
    objectMapper.writeValue(tmpFile, object);
    tmpFile.deleteOnExit();

    try{
      var multipart =  create(tmpFile, formFieldName);
      if (!tmpFile.delete()) {
        log.error("Can't delete temp file ={}, would be deleted automatically on exit",
            tmpFile.getAbsolutePath());
      }
      return multipart;
    } catch (IOException e) {
      if (!tmpFile.delete()) {
        log.error("Can't delete temp file ={}, would be deleted automatically on exit",
            tmpFile.getAbsolutePath());
      }
      throw e;
    }
  }
}
