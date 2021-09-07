package collaborate.api.passport.find;

import static collaborate.api.passport.model.storage.StorageFields.INDEXER_PASSPORTS_BY_DSP;
import static collaborate.api.passport.model.storage.StorageFields.INDEXER_PASSPORTS_BY_VO;
import static collaborate.api.passport.model.storage.StorageFields.INDEXER_PASSPORTS_WAITING_CONSENT_BY_DSP;
import static collaborate.api.passport.model.storage.StorageFields.INDEXER_PASSPORTS_WAITING_CONSENT_BY_VO;
import static collaborate.api.passport.model.storage.StorageFields.MULTISIGS;
import static collaborate.api.passport.model.storage.StorageFields.PASSPORT_METADATA_BY_TOKEN_ID;
import static java.util.stream.Collectors.toList;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayStorageClient;
import collaborate.api.tag.model.Key;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.IndexerQuery;
import collaborate.api.tag.model.storage.IndexerQueryResponse;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FindPassportDAO {


  private final ApiProperties apiProperties;
  private final DigitalPassportDTOFactory digitalPassportDTOFactory;
  private final TezosApiGatewayPassportClient tezosApiGatewayPassportClient;
  private final TezosApiGatewayStorageClient tezosApiGatewayStorageClient;

  public PassportIdsDTO findPassportsIdByVo(String vehicleOwnerAccountAddress) {
    var passportIdByVO = new DataFieldsRequest<>(List.of(
        new IndexerQuery<>(INDEXER_PASSPORTS_WAITING_CONSENT_BY_VO, vehicleOwnerAccountAddress),
        new IndexerQuery<>(INDEXER_PASSPORTS_BY_VO, vehicleOwnerAccountAddress)
    ));
    IndexerQueryResponse<Integer> passportIdsResponse = tezosApiGatewayStorageClient
        .queryIndexer(apiProperties.getContractAddress(), passportIdByVO);

    return new PassportIdsDTO(
        passportIdsResponse.getFirstEntryValue(INDEXER_PASSPORTS_WAITING_CONSENT_BY_VO),
        passportIdsResponse.getFirstEntryValue(INDEXER_PASSPORTS_BY_VO)
    );
  }

  public PassportIdsDTO findPassportsIdByDsp(String organizationAccountAddress) {
    var passportIdByDsp = new DataFieldsRequest<>(List.of(
        new IndexerQuery<>(INDEXER_PASSPORTS_WAITING_CONSENT_BY_DSP, organizationAccountAddress),
        new IndexerQuery<>(INDEXER_PASSPORTS_BY_DSP, organizationAccountAddress)
    ));
    IndexerQueryResponse<Integer> passportIdsResponse = tezosApiGatewayStorageClient
        .queryIndexer(apiProperties.getContractAddress(), passportIdByDsp);

    return new PassportIdsDTO(
        passportIdsResponse.getFirstEntryValue(INDEXER_PASSPORTS_WAITING_CONSENT_BY_DSP),
        passportIdsResponse.getFirstEntryValue(INDEXER_PASSPORTS_BY_DSP)
    );
  }

  public Collection<DigitalPassportDTO> findPassportsByIds(PassportIdsDTO passportIdsDTO) {
    var multisigQuery = new IndexerQuery<>(MULTISIGS,
        passportIdsDTO.getPassportsWaitingConsent().stream()
            .map(id -> new Key<>(id.toString()))
            .collect(toList())
    );
    var passportMetadataQuery = new IndexerQuery<>(PASSPORT_METADATA_BY_TOKEN_ID,
        passportIdsDTO.getPassportsConsented().stream()
            .map(id -> new Key<>(id.toString()))
            .collect(toList())
    );
    var request = new DataFieldsRequest<>(List.of(multisigQuery, passportMetadataQuery));

    return toDigitalPassportDTO(tezosApiGatewayPassportClient
        .findPassportsByIds(apiProperties.getContractAddress(), request));
  }

  private Collection<DigitalPassportDTO> toDigitalPassportDTO(PassportByIdsDTO passportByIdsDTO) {
    return Stream.concat(
        passportByIdsDTO.getMultisigs().stream().map(digitalPassportDTOFactory::fromMultisig),
        passportByIdsDTO.getPassportMetadataByTokenId().stream()
            .map(digitalPassportDTOFactory::fromPassportMetadata)
    ).collect(toList());
  }

}
