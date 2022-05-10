package collaborate.api.datasource.passport.consent;

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

  private ConsentPassportDAO consentPassportDAO;

  @BeforeEach
  void setUp() {
    TransactionBatchFactory transactionBatchFactory = new TransactionBatchFactory();
    String businessDataContractAddress = "KT1CucfmZNzz3cwxvR8dGtLzxqnkzBvdRJ2t";
    consentPassportDAO = new ConsentPassportDAO(
        tezosApiGatewayJobClient,
        transactionBatchFactory,
        businessDataContractAddress
    );
  }

  @Test
  void consent_shouldCallSendTransactionBatchWithExpectedParam() {
    //GIVEN
    Job mockedJobResult = Job.builder()
        .id(2)
        .status(Status.CREATED)
        .build();
    String userId = "development_._xdev-at_._theblockchainxdev.com";
    var consentPassportDTO = ConsentPassportDTO.builder()
        .contractId(21)
        .vehicleOwnerUserWallet(UserWalletDTO.builder()
            .userId(userId)
            .address("tz1hEiMos9CF5Z5MoFyhZMgSiRKzyDZRdoBG")
            .build()
        ).build();

    when(
        tezosApiGatewayJobClient.sendTransactionBatch(
            ConsentPassportFeatures.consentPassportTransactionBatch,
            false
        )
    ).thenReturn(mockedJobResult);

    //WHEN
    Job actual = consentPassportDAO.consent(consentPassportDTO);
    //THEN
    verify(tezosApiGatewayJobClient, times(1))
        .sendTransactionBatch(
            ConsentPassportFeatures.consentPassportTransactionBatch,
            false
        );
  }

}
