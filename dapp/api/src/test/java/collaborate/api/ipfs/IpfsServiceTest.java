package collaborate.api.ipfs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IpfsServiceTest {

  @InjectMocks
  IpfsService ipfsService;


  @Test
  void cleanUri_shouldRemoveIpfsProtocol() {
    // GIVEN
    String address = "ipfs://QmWN67CHJdi7jHrqtJaAwQj3uBC8qumb2zcWXp7sMNsBbH";
    String expectedAddressResult = "QmWN67CHJdi7jHrqtJaAwQj3uBC8qumb2zcWXp7sMNsBbH";
    // WHEN
    var currentAddressResult = ipfsService.cleanUri(address);
    // THEN
    assertThat(currentAddressResult).isEqualTo(expectedAddressResult);
  }

  @Test
  void cleanUri_shouldRemoveIpnsProtocolAndAndIpnsPrefix() {
    // GIVEN
    String address = "ipns://k51qzi5uqu5diffq8lulcv4ovfcbobao0wcc50bt0k06i2m0gujx78bddfnxa4/DigitalPassport/20211011/A73EMQ003KVNOI3YP_1633942829565";
    String expectedAddressResult = "/ipns/k51qzi5uqu5diffq8lulcv4ovfcbobao0wcc50bt0k06i2m0gujx78bddfnxa4/DigitalPassport/20211011/A73EMQ003KVNOI3YP_1633942829565";
    // WHEN
    var currentAddressResult = ipfsService.cleanUri(address);
    // THEN
    assertThat(currentAddressResult).isEqualTo(expectedAddressResult);
  }
}
