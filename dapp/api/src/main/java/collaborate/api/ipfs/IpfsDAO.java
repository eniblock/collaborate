package collaborate.api.ipfs;

import collaborate.api.http.MultipartFactory;
import collaborate.api.ipfs.domain.LsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Repository
public class IpfsDAO {

  private final MultipartFactory multipartFactory;
  private final IpfsClient ipfsClient;
  private final IpfsFilesClient ipfsFilesClient;
  private final ObjectMapper objectMapper;
  private final IpfsPinClient ipfsPinClient;

  public String addDirectory(String path) {
    ipfsFilesClient.makeDirectory(path, true);
    var cid = ipfsFilesClient.flush(path);
    return cid.getCid();
  }

  /**
   * @param file file to add in IPFS
   * @return The hash for the given file
   */
  public String add(File file) throws IOException {
    MultipartFile providerMultipartFile = multipartFactory.create(file, "file");
    var addResponse = ipfsClient.add(providerMultipartFile);
    return addResponse.getHash();
  }

  /** @return The created file IPFS hash */
  public <T> String add(Path filePath, T object) throws IOException {
    ipfsFilesClient.makeDirectory(filePath.getParent().toString(), true);
    var multipartFile = multipartFactory.create(object, "data");
    ipfsFilesClient.write(filePath.toString(), true, multipartFile);

    var fileCid = ipfsFilesClient.flush(filePath.toString());
    ipfsPinClient.add(fileCid.getCid());
    log.info("{} added with hash={}", filePath, fileCid.getCid());

    return fileCid.getCid();
  }

  // FIXME might not found data
  public <T> T cat(String hash, Class<T> clazz){

    try {
      return objectMapper.readValue(ipfsClient.cat(hash), clazz);
    } catch (JsonProcessingException e) {
      log.error("can't deserialize IPFS cid={} to class={}", hash, clazz.getName());
      throw new IllegalStateException(e);
    }
  }

  public <T> T cat(String hash, TypeReference<T> type) {
    try {
      return objectMapper.readValue(ipfsClient.cat(hash), type);
    } catch (JsonProcessingException e) {
      log.error("can't deserialize IPFS cid={} to class={}", hash, type.getType().getTypeName());
      throw new IllegalStateException(e);
    }
  }

  public LsResponse listDirectoryContent(String cid) {
    return ipfsClient.listDirectoryContent(cid);
  }

  public String cat(String cid) {
    return ipfsClient.cat(cid);
  }
}
