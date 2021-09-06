package collaborate.api.passport.find;


import collaborate.api.organization.OrganizationDAO;
import collaborate.api.passport.DigitalPassportStatus;
import collaborate.api.passport.model.storage.Multisig;
import collaborate.api.passport.model.storage.PassportMetadata;
import collaborate.api.tag.model.TagEntry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigitalPassportDTOFactory {

  private final OrganizationDAO organizationDAO;

  public DigitalPassportDTO fromMultisig(TagEntry<Integer, Multisig> multisigEntry) {
    var multisig = multisigEntry.getValue();
    return DigitalPassportDTO.builder()
        .datasourceUUID(UUID.fromString(multisig.getParam2()))
        .vin(multisig.getParam1())
        .dspAddress(multisig.getAddr1())
        .dspName(
            organizationDAO.getOrganizationByPublicKeyHash(multisig.getAddr1())
                .orElseThrow().getName())
        .status(DigitalPassportStatus.PENDING_CREATION)
        .contractId(multisigEntry.getKey())
        .createdAt(multisig.getCreatedAt())
        .build();
  }

  public DigitalPassportDTO fromPassportMetadata(
      TagEntry<Integer, PassportMetadata> passportMetadataTagEntry) {
    var passportMetadata = passportMetadataTagEntry.getValue();
    return DigitalPassportDTO.builder()
        .datasourceUUID(UUID.fromString(passportMetadata.getDatasourceUUID()))
        .vin(passportMetadata.getVin())
        .dspAddress(passportMetadata.getDspAddress())
        .dspName(
            organizationDAO.getOrganizationByPublicKeyHash(passportMetadata.getDspAddress())
                .orElseThrow().getName())
        .status(DigitalPassportStatus.CREATED)
        .contractId(passportMetadata.getMultisigId())
        .tokenId(passportMetadataTagEntry.getKey())
        .createdAt(passportMetadata.getCreatedAt())
        .build();
  }
}
