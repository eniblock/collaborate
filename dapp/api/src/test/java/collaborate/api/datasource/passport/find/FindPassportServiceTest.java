package collaborate.api.datasource.passport.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.organization.OrganizationService;
import collaborate.api.test.TestResources;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindPassportServiceTest {

  @Mock
  FindPassportDAO findPassportDAO;
  @Mock
  OrganizationService organizationService;

  @InjectMocks
  FindPassportService findPassportService;


}
