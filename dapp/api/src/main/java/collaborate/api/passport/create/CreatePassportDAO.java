package collaborate.api.passport.create;

import static java.lang.String.format;

import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.tag.TagUserDAO;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePassportDAO {

  private final TagUserDAO tagUserDAO;
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;

  private static final String CREATION_ENTRY_POINT = "initPassportCreation";

  public Job create(CreatePassportDTO createPassportDTO) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        CREATION_ENTRY_POINT,
        buildCreateEntryPointParam(createPassportDTO),
        Optional.empty()
    );

    return tezosApiGatewayJobClient.sendTransactionBatch(transactions);
  }

  private InitPassportCreationEntryPointParam buildCreateEntryPointParam(
      CreatePassportDTO createPassportDTO) {

    String vehicleOwnerAddress = tagUserDAO
        .findOneByUserId(createPassportDTO.getVehicleOwnerMail())
        .map(UserWalletDTO::getAddress)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, (
            format("No user found for userId=%s", createPassportDTO.getVehicleOwnerMail())
        )));

    String dspAddress = tagUserDAO.getOrganizationAccountAddress();

    return InitPassportCreationEntryPointParam.builder()
        .vehicleOwnerAddress(vehicleOwnerAddress)
        .dspAddress(dspAddress)
        .vin(createPassportDTO.getVin())
        .datasourceUUID(createPassportDTO.getDatasourceUUID().toString())
        .build();
  }

}
