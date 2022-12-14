package collaborate.api.ipfs;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.ipfs.domain.CidResponse;
import collaborate.api.ipfs.domain.IpnsResponse;
import collaborate.api.ipfs.domain.ListKeyPairResponse;
import collaborate.api.ipfs.domain.dto.IpnsFoldersDTO;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IpnsServiceTest {

  @Mock
  IpfsFilesClient ipfsFilesClient;
  @Mock
  IpfsNameClient ipfsNameClient;
  @Mock
  IpfsPinClient ipfsPinClient;
  @Mock
  IpfsKeyClient ipfsKeyClient;
  @Spy
  IpnsFoldersDTO ipnsFolders;
  @InjectMocks
  IpnsService ipnsService;

  @Test
  void initIpnsFolder_shouldCallExpectedWorkflowAndReturnIpns()
      throws ExecutionException, InterruptedException {
    String folderPath = "/this/is/root/folder";
    CidResponse cid = readContent("/ipfs/init-folder/files.flush.json", CidResponse.class);
    ListKeyPairResponse listKeyPairResponse = readContent("/ipfs/init-folder/key.list.json",
        ListKeyPairResponse.class);
    var publishResponse = readContent("/ipfs/init-folder/name.publish.json", IpnsResponse.class);
    when(ipfsFilesClient.flush(folderPath)).thenReturn(cid);
    when(ipfsKeyClient.getAllKeyPairs()).thenReturn(listKeyPairResponse);
    when(ipfsNameClient.update(cid.getCid(), "12345")).thenReturn(publishResponse);

    // WHEN
    var completableResult = ipnsService.initIpnsFolder(folderPath);
    var ipns = completableResult.get();
    // THEN
    verify(ipfsFilesClient, times(1)).makeDirectory(folderPath, true);
    verify(ipfsFilesClient, times(1)).flush(folderPath);
    verify(ipfsPinClient, times(1)).add(cid.getCid());
    assertThat(ipns).isEqualTo(publishResponse.getIpns());
  }

  @Test
  void initIpnsFolder_shouldUpdateIpnsFolders() {
    // GIVEN
    var ipnsFolder = "/test/folderA";
    // WHEN
    ipnsService.initIpnsFolder(ipnsFolder);
    // THEN
    verify(ipnsFolders).add(ipnsFolder);
  }

  @Test
  void getKeyPairByName_find() {
    ListKeyPairResponse listKeyPairResponse = readContent("/ipfs/init-folder/key.list.json",
        ListKeyPairResponse.class);
    when(ipfsKeyClient.getAllKeyPairs()).thenReturn(listKeyPairResponse);

    // WHEN
    var result = ipnsService.getKeyPairByName("/this/is/root/folder");
    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("/this/is/root/folder");
    assertThat(result.get().getId()).isEqualTo("12345");
  }

  @Test
  void getKeyPairByName_notFind() {
    ListKeyPairResponse listKeyPairResponse = readContent("/ipfs/init-folder/key.list.json",
        ListKeyPairResponse.class);
    when(ipfsKeyClient.getAllKeyPairs()).thenReturn(listKeyPairResponse);

    // WHEN
    var result = ipnsService.getKeyPairByName("/unfound/folder");
    // THEN
    assertThat(result).isNotPresent();
  }

}
