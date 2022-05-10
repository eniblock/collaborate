package collaborate.api.datasource.passport.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.SmartContractAddressProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CreatePassportDAOTest {

  @Mock
  private TezosApiGatewayJobClient tezosApiGatewayJobClient;
  @Mock
  private TransactionBatchFactory transactionBatchFactory;
  @Mock
  private SmartContractAddressProperties smartContractAddressProperties;
  @Mock
  private TezosApiGatewayPassportCreationClient tezosApiGatewayPassportCreationClient;
  @Mock
  private UserService userService;

  @InjectMocks
  CreatePassportDAO createPassportDAO;


  @Test
  void create_shouldReturnMockedJob() {
    //GIVEN
    Job mockedJob = initSomeJob();

    when(tezosApiGatewayJobClient.sendTransactionBatch(any(), eq(false))).thenReturn(mockedJob);
    when(tezosApiGatewayPassportCreationClient.getMultisigNb(any(), any())).thenReturn(
        MultisigNbResponseDTO.builder().multisigNb(1).build()
    );
    when(userService.getAdminUser()).thenReturn(
        UserWalletDTO.builder().address("tz1WpmFuSZfuNS7XDKwDZxX3QhSNUneTkiTv").build()
    );
    CreateMultisigPassportDTO passportFromFrontend = initPassport();
    UserWalletDTO userWalletDTO = initWallet();
    //WHEN
    Job actual = createPassportDAO.create(
        "ipfs://my_uri",
        userWalletDTO.getAddress());
    //THEN
    assertThat(actual.getId()).isEqualTo(mockedJob.getId());
    assertThat(actual.getStatus()).isEqualTo(mockedJob.getStatus());
  }

  private UserWalletDTO initWallet() {
    return UserWalletDTO.builder()
        .userId("admin")
        .address("tz1QdgwqsVV7SmpFPrWjs9B5oBNcj2brzqfG")
        .build();
  }

  private Job initSomeJob() {
    Job job = new Job();
    job.setId(1);
    job.setStatus(Job.Status.CREATED);
    return job;
  }

  private CreateMultisigPassportDTO initPassport() {
    return CreateMultisigPassportDTO.builder()
        .assetOwnerMail("alice@theblockchainxdev.com")
        .assetId("LE_VIN")
        .assetIdForDatasource("ASSET_ID_IN_DATASOURCE")
        .datasourceUUID(UUID.fromString("ab357d94-04da-4695-815e-24c569fd3a49"))
        .build();
  }

}
