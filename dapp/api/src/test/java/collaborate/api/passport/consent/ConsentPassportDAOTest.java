package collaborate.api.passport.consent;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.Job.Status;
import collaborate.api.tag.model.user.UserWalletDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsentPassportDAOTest {

  @Mock
  private TezosApiGatewayJobClient tezosApiGatewayJobClient;
  @Mock
  private ApiProperties apiProperties;

  private ConsentPassportDAO consentPassportDAO;
  private TransactionBatchFactory transactionBatchFactory;

  @BeforeEach
  void setUp() {
    when(apiProperties.getContractAddress()).thenReturn("KT1CucfmZNzz3cwxvR8dGtLzxqnkzBvdRJ2t");
    transactionBatchFactory = new TransactionBatchFactory(apiProperties);
    consentPassportDAO = new ConsentPassportDAO(tezosApiGatewayJobClient, transactionBatchFactory);
  }

  @Test
  void consent_shouldCallSendTransactionBatchWithExpectedParam() {
    //GIVEN
    Job mockedJob = Job.builder()
        .id(2)
        .status(Status.CREATED)
        .build();
    var consentPassportDTO = ConsentPassportDTO.builder()
        .contractId(21)
        .vehicleOwnerUserWallet(UserWalletDTO.builder()
            .userId("pcc-development_._xdev-at_._theblockchainxdev.com")
            .address("tz1hEiMos9CF5Z5MoFyhZMgSiRKzyDZRdoBG")
            .build()
        ).build();

    when(tezosApiGatewayJobClient
        .sendTransactionBatch(eq(ConsentPassportFeatures.consentPassportTransactionBatch), eq(false))
    ).thenReturn(mockedJob);

    //WHEN
    Job actual = consentPassportDAO.consent(consentPassportDTO);
    //THEN
    verify(tezosApiGatewayJobClient, times(1))
        .sendTransactionBatch(eq(ConsentPassportFeatures.consentPassportTransactionBatch), eq(false));
  }

}