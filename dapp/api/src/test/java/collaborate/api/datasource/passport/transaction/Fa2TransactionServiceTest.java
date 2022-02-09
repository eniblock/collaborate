package collaborate.api.datasource.passport.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.passport.model.transaction.Fa2Transaction;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Fa2TransactionServiceTest {

  @Mock
  Fa2TransactionDAO fa2TransactionDAO;

  @InjectMocks
  Fa2TransactionService fa2TransactionService;

  @Test
  void getTransactionDateByTokenId() {
    //GIVEN
    var expected = makeTransaction();
    when(fa2TransactionDAO
        .findBySmartContractAndTokenId(anyString(), anyLong()))
        .thenReturn(Optional.of(expected));
    //WHEN
    var actual = fa2TransactionService
        .findTransactionDateByTokenId("SMART_CONTRACT", 1L);
    //THEN
    assertThat(actual).hasValue(expected.getTimestamp());
  }

  private Fa2Transaction makeTransaction() {
    return Fa2Transaction.builder()
        .smartContract("SMART_CONTRACT")
        .tokenId(1L)
        .entrypoint("build")
        .owner("owner")
        .ipfsMetadata("ipfs_uri")
        .timestamp(ZonedDateTime.now())
        .parameters(null)
        .build();
  }
}
