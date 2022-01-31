package collaborate.api.datasource.multisig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProxyTokenControllerTransactionServiceTest {

  @Mock
  ProxyTokenControllerTransactionDAO proxyTokenControllerTransactionDAO;

  @InjectMocks
  ProxyTokenControllerTransactionService proxyTokenControllerTransactionService;

  @Test
  void findTransactionByTokenId() {
    //GIVEN
    var expected = makeTransaction();
    when(proxyTokenControllerTransactionDAO
        .findBySmartContractAndMultiSigId(anyString(), anyLong()))
        .thenReturn(Optional.of(expected));
    //WHEN
    var actual = proxyTokenControllerTransactionService
        .findTransactionByTokenId("SMART_CONTRACT", 1L);
    //THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void findMultiSigListTransactionByOwner() {
    //GIVEN
    var expected = List.of(makeTransaction());
    when(proxyTokenControllerTransactionDAO
        .findAllBySmartContractAndEntrypointAndIsSignedAndOwner(
            anyString(), anyString(), anyBoolean(), anyString()))
        .thenReturn(expected);
    //WHEN
    var actual = proxyTokenControllerTransactionService
        .findMultiSigListTransactionByOwner("SMART_CONTRACT", "owner");
    //THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void findMultiSigListTransactionByOperator() {
    var expected = List.of(makeTransaction());
    when(proxyTokenControllerTransactionDAO
        .findAllBySmartContractAndEntrypointAndIsSignedAndOperator(
            anyString(), anyString(), anyBoolean(), anyString()))
        .thenReturn(expected);
    //WHEN
    var actual = proxyTokenControllerTransactionService
        .findMultiSigListTransactionByOperator("SMART_CONTRACT", "operator");
    //THEN
    assertThat(actual).isEqualTo(expected);
  }

  private ProxyTokenControllerTransaction makeTransaction() {
    return ProxyTokenControllerTransaction.builder()
        .smartContract("SMART_CONTRACT")
        .multiSigId(1L)
        .entrypoint("build")
        .isSigned(false)
        .owner("owner")
        .operator("operator")
        .metadata("ipfs_uri")
        .timestamp(ZonedDateTime.now())
        .build();
  }
}
