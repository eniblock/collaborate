package collaborate.api.ipfs;

import collaborate.api.ipfs.domain.KeyPair;
import collaborate.api.ipfs.domain.dto.IpnsFoldersDTO;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class IpnsService {

  private final IpfsFilesClient ipfsFilesClient;
  private final IpfsNameClient ipfsNameClient;
  private final IpfsPinClient ipfsPinClient;
  private final IpfsKeyClient ipfsKeyClient;
  private final IpnsFoldersDTO ipnsFolders;

  public CompletableFuture<String> initIpnsFolder(String absolutePath) {
    ipnsFolders.add(absolutePath);
    return CompletableFuture.supplyAsync(() -> {
      ipfsFilesClient.makeDirectory(absolutePath, true);
      var cid = ipfsFilesClient.flush(absolutePath);
      ipfsPinClient.add(cid.getCid());
      var keyPairResponse = getKeyPairOrCreateIt(absolutePath);
      var publishResponse = ipfsNameClient.update(cid.getCid(), keyPairResponse.getId());
      log.info("ipfs path={} published with ipns={}", absolutePath, publishResponse.getIpns());
      return publishResponse.getIpns();
    });
  }

  public Optional<KeyPair> getKeyPairByName(String name) {
    return ipfsKeyClient.getAllKeyPairs().getKeys()
        .stream()
        .filter(keyPair -> keyPair.getName().equals(name))
        .findFirst();
  }

  private KeyPair getKeyPairOrCreateIt(String absolutePath) {
    return getKeyPairByName(absolutePath)
        .orElseGet(() -> {
          log.info("No IPNS key found for name={}, a new one will be created", absolutePath);
          return ipfsKeyClient.createKeyPair(absolutePath);
        });
  }

  public void updateKeys() {
    ipnsFolders.getIpnsFolders().stream()
        .map(this::initIpnsFolder)
        .forEach(future ->
            future.whenComplete(
                (result, ex) -> {
                  if (ex != null) {
                    log.error("failed updating ipns folder", ex);
                  }
                }
            )
        );
  }

}

