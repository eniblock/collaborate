package collaborate.api.datasource.passport.create;

import static collaborate.api.mail.EMailService.NOREPLY_THEBLOCKCHAINXDEV_COM;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.catalog.create.Tzip21MetadataService;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.mail.EMailDTO;
import collaborate.api.mail.EMailService;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.UserService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePassportService {

  private static final String CONTACT_EMAIL_HTML_TEMPLATE = "html/contactEmail.html";

  private final CreatePassportDAO createPassportDAO;
  private final DateFormatterFactory dateFormatterFactory;
  private final EMailService eMailService;
  private final PassportTzip21MetadataFactory passportTzip21MetadataFactory;
  private final TokenMetadataProperties tokenMetadataProperties;
  private final Tzip21MetadataService tzip21MetadataService;
  private final UserService userService;
  private final UUIDGenerator uuidGenerator;

  public Job createMultisig(CreateMultisigPassportDTO createMultisigPassportDTO)
      throws IOException {
    String assetOwnerWalletAddress = userService
        .findWalletAddressByEmailOrThrow(createMultisigPassportDTO.getAssetOwnerMail());
    AssetDTO assetDTO = buildAssetDTO(createMultisigPassportDTO);
    var ipfsMetadataUri = tzip21MetadataService.saveMetadata(assetDTO);
    var job = createPassportDAO.create(
        ipfsMetadataUri,
        assetOwnerWalletAddress
    );
    sendMultisigCreatedEmail(createMultisigPassportDTO.getAssetOwnerMail());
    return job;
  }

  private AssetDTO buildAssetDTO(CreateMultisigPassportDTO createMultisigPassportDTO) {
    return AssetDTO.builder()
        .assetRelativePath(buildAssetRelativePath())
        .assetIdForDatasource(createMultisigPassportDTO.getAssetIdForDatasource())
        .datasourceUUID(createMultisigPassportDTO.getDatasourceUUID())
        .assetType("digital-passport")
        .tZip21Metadata(passportTzip21MetadataFactory.create(createMultisigPassportDTO.getAssetId()))
        .build();
  }

  String buildAssetRelativePath() {
    return Path.of(
        dateFormatterFactory.forPattern(
            tokenMetadataProperties.getAssetDataCatalogPartitionDatePattern()
        ),
        uuidGenerator.randomUUID().toString()
    ).toString();
  }

  void sendMultisigCreatedEmail(String recipient) {
    log.info("SendPassportMail({})", recipient);

    EMailDTO EMailDTO = new EMailDTO(
        NOREPLY_THEBLOCKCHAINXDEV_COM,
        recipient,
        "Your Digital Passport Creation",
        Map.of(
            "content",
            "You received this mail because your passport is created and await your consent."
        )
    );

    try {
      eMailService.sendMail(EMailDTO, StandardCharsets.UTF_8.name(), CONTACT_EMAIL_HTML_TEMPLATE);
    } catch (MessagingException | MailException e) {
      log.error("Problem with Mail sending", e);
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR,
          "Passport was created, but server failed to send mail to Vehicle owner !"
      );
    }
  }

}
