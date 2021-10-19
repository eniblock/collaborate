package collaborate.api.passport.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.mail.MailService;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.tag.TagUserDAO;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatePassportDAOTest {

  @Mock
  private TezosApiGatewayJobClient tezosApiGatewayJobClient;
  @Mock
  private TransactionBatchFactory transactionBatchFactory;
  @Mock
  private TagUserDAO tagUserDAO;
  @Mock
  private ApiProperties apiProperties;
  @Mock
  private MailService mailService;

  @InjectMocks
  CreatePassportDAO createPassportDAO;

  @Test
  void create_shouldReturnMockedJob() {
    //GIVEN
    Job mockedJob = initSomeJob();

    when(apiProperties.getDigitalPassportContractAddress())
        .thenReturn("KT1CucfmZNzz3cwxvR8dGtLzxqnkzBvdRJ2t");
    when(tezosApiGatewayJobClient.sendTransactionBatch(any(), eq(false))).thenReturn(mockedJob);
    CreateMultisigPassportDTO passportFromFrontend = initPassport();
    UserWalletDTO userWalletDTO = initWallet();
    //WHEN
    Job actual = createPassportDAO.create(
        "ipfs://my_uri",
        userWalletDTO.getAddress(),
        passportFromFrontend.getAssetId());
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
