package collaborate.api.passport.create;

import static java.lang.String.format;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.Job;
import collaborate.api.tag.model.Transaction;
import collaborate.api.tag.model.TransactionBatch;
import collaborate.api.user.tag.TagUserService;
import collaborate.api.user.tag.UserWalletDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagCreatePassportService {

  private final ApiProperties apiProperties;
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TagUserService tagUserService;

  private static final String CREATION_ENTRY_POINT = "initPassportCreation";
  private static final String SECURE_KEY_NAME = "admin";

  public Job create(CreatePassportDTO createPassportDTO) {

    Transaction<InitPassportCreationEntryPointParam> transaction = Transaction
        .<InitPassportCreationEntryPointParam>builder()
        .entryPoint(CREATION_ENTRY_POINT)
        .contractAddress(apiProperties.getContractAddress())
        .entryPointParams(buildCreateEntryPointParam(createPassportDTO))
        .build();

    TransactionBatch<InitPassportCreationEntryPointParam> transactions = new TransactionBatch<>();
    transactions.setTransactions(List.of(transaction));
    transactions.setSecureKeyName(SECURE_KEY_NAME);

    return tezosApiGatewayJobClient.sendTransactionBatch(transactions);
  }

  private InitPassportCreationEntryPointParam buildCreateEntryPointParam(
      CreatePassportDTO createPassportDTO) {

    String vehicleOwnerAddress = tagUserService
        .findOneByUserId(createPassportDTO.getVehicleOwnerMail())
        .map(UserWalletDTO::getAddress)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, (
            format("No user found for userId=%s", createPassportDTO.getVehicleOwnerMail())
        )));

    String dspAddress = tagUserService.getDSPAddress();

    return InitPassportCreationEntryPointParam.builder()
        .vehicleOwnerAddress(vehicleOwnerAddress)
        .dspAddress(dspAddress)
        .vin(new Bytes(createPassportDTO.getVin()))
        .datasourceUUID(new Bytes(createPassportDTO.getDatasourceUUID().toString()))
        .build();
  }


}
