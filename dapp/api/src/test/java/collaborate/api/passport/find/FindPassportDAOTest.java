package collaborate.api.passport.find;

import static collaborate.api.passport.find.FindPassportFeatures.findPassportsByIdsResponseWithEmptyPassportMetadataTokenById;
import static collaborate.api.passport.find.FindPassportFeatures.findPassportsIdByVoResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.OrganizationDAO;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.passport.DigitalPassportStatus;
import collaborate.api.tag.TezosApiGatewayStorageClient;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.IndexerQuery;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindPassportDAOTest {

  @Mock
  private ApiProperties apiProperties;
  @Mock
  private TezosApiGatewayStorageClient tezosApiGatewayStorageClient;
  @Mock
  private TezosApiGatewayPassportClient tezosApiGatewayPassportClient;
  @Mock
  private OrganizationDAO organizationDAO;

  private FindPassportDAO findPassportDAO;

  @Captor
  private ArgumentCaptor<DataFieldsRequest<IndexerQuery<String>>> indexerQueryRequestCaptor;

  private DigitalPassportDTOFactory digitalPassportDTOFactory;
  private final String contractAdress = "KT1X6oE16gCKpbvgLqSk3P3dxWXT8vJh7rr9";

  @BeforeEach
  void setUp() {
    digitalPassportDTOFactory = new DigitalPassportDTOFactory(organizationDAO);
    findPassportDAO = new FindPassportDAO(
        apiProperties,
        digitalPassportDTOFactory,
        tezosApiGatewayPassportClient,
        tezosApiGatewayStorageClient
    );
    when(apiProperties.getContractAddress()).thenReturn(contractAdress);
  }

  @Test
  void findPassportsIdByVo_returnExpectedPassportIdsDTO() {
    // GIVEN
    String vehicleOwnerAddress = "tzVoAddress";

    // WHEN
    when(tezosApiGatewayStorageClient.<Integer, String>queryIndexer(
        eq(contractAdress),
        indexerQueryRequestCaptor.capture()
    )).thenReturn(findPassportsIdByVoResponse);
    var passportIds = findPassportDAO.findPassportsIdByVo(vehicleOwnerAddress);
    // THEN
    assertThat(passportIds.getPassportsConsented()).isNullOrEmpty();
    assertThat(passportIds.getPassportsWaitingConsent()).containsExactlyInAnyOrder(1);
  }

  @Test
  void findPassportsByIds_returnExpectedDigitalPassportDTO() {
    // GIVEN
    when(tezosApiGatewayPassportClient.findPassportsByIds(eq(contractAdress), any()))
        .thenReturn(findPassportsByIdsResponseWithEmptyPassportMetadataTokenById);

    String dspAddress = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV";
    String dspName = "psa";
    when(organizationDAO.getOrganizationByPublicKeyHash(dspAddress))
        .thenReturn(Optional.of(new OrganizationDTO(dspName, null, null)));

    var expectedDigitalPassport = DigitalPassportDTO.builder()
        .dspAddress(dspAddress)
        .dspName(dspName)
        .datasourceUUID(UUID.fromString("79cf3653-cd79-4ac9-8b6d-90f8d35a4454"))
        .vin("WO0P6Z5QF9FRMXAGC")
        .contractId(1)
        .status(DigitalPassportStatus.PENDING_CREATION)
        .createdAt(LocalDateTime.of(2021, 9, 5, 15, 43, 42))
        .build();
    // WHEN
    var digitalPassportDTOs = findPassportDAO.findPassportsByIds(new PassportIdsDTO());
    // THEN
    assertThat(digitalPassportDTOs).containsExactlyInAnyOrder(expectedDigitalPassport);
  }
}
