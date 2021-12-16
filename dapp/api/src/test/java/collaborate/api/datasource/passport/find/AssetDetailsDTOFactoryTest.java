package collaborate.api.datasource.passport.find;

import static collaborate.api.datasource.passport.model.AccessStatus.GRANTED;
import static collaborate.api.datasource.passport.model.AccessStatus.LOCKED;
import static collaborate.api.datasource.passport.model.AccessStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.nft.model.storage.Multisig;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetDetailsDTOFactoryTest {

  @Mock
  UserService userService;

  @Mock
  ConnectedUserService connectedUserService;

  @InjectMocks
  DigitalPassportDetailsDTOFactory digitalPassportDetailsDTOFactory;


  
}
