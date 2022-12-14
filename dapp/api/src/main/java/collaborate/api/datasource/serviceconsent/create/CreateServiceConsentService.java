package collaborate.api.datasource.serviceconsent.create;

import static collaborate.api.mail.EMailService.NOREPLY_THEBLOCKCHAINXDEV_COM;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.catalog.create.Tzip21MetadataService;
import collaborate.api.datasource.passport.create.PassportTzip21MetadataFactory;
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
public class CreateServiceConsentService {

  private static final String CONTACT_EMAIL_HTML_TEMPLATE = "html/contactEmail.html";

  private final CreateServiceConsentDAO createServiceConsentDAO;
  private final DateFormatterFactory dateFormatterFactory;
  private final EMailService mailService;
  private final ConsentTzip21MetadataService tzip21MetadataService;
  private final ConsentServiceTzip21MetadataFactory consentTzip21MetadataFactory;
  private final TokenMetadataProperties tokenMetadataProperties;
  private final UserService userService;
  private final UUIDGenerator uuidGenerator;

  public Job createMultisig(CreateMultisigServiceConsentDTO createMultisigServiceConsentDTO)
      throws IOException {
    String assetOwnerWalletAddress = userService
        .findWalletAddressByEmailOrThrow(createMultisigServiceConsentDTO.getAssetOwnerMail());
    AssetDTO assetDTO = buildAssetDTO(createMultisigServiceConsentDTO);
    var ipfsMetadataUri = tzip21MetadataService.saveMetadata(assetDTO);
    var job = createServiceConsentDAO.create(
        ipfsMetadataUri,
        assetOwnerWalletAddress
    );
    sendMultisigCreatedEmail(createMultisigServiceConsentDTO.getAssetOwnerMail());
    return job;
  }

  private AssetDTO buildAssetDTO(CreateMultisigServiceConsentDTO createMultisigServiceConsentDTO) {
    return AssetDTO.builder()
        .assetRelativePath(buildAssetRelativePath())
        .assetIdForDatasource(createMultisigServiceConsentDTO.getAssetIdForDatasource())
        .datasourceUUID(createMultisigServiceConsentDTO.getAssetId()) //createMultisigServiceConsentDTO.getDatasourceUUID())
        .assetType("digital-serviceconsent")
        .tZip21Metadata(consentTzip21MetadataFactory.create(createMultisigServiceConsentDTO.getAssetId().toString())) // +"_"+createMultisigServiceConsentDTO.getPassportId().toString()
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
    log.info("SendServiceConsentMail({})", recipient);

    EMailDTO mailDTO = new EMailDTO(
        NOREPLY_THEBLOCKCHAINXDEV_COM,
        recipient,
        "Your Digital ServiceConsent Creation",
        Map.of(
            "content",
            "You received this mail because your serviceconsent is created and await your consent."
        ));
    log.info("mailDTO={}", mailDTO);

    try {
      mailService.sendMail(mailDTO, StandardCharsets.UTF_8.name(), CONTACT_EMAIL_HTML_TEMPLATE);
    } catch (MessagingException | MailException e) {
      log.error("Problem with Mail sending", e);
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR,
          "ServiceConsent was created, but server failed to send mail to Vehicle owner !"
      );
    }
  }

}
