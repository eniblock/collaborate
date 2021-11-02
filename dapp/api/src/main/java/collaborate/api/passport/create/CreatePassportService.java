package collaborate.api.passport.create;

import static collaborate.api.mail.MailService.NOREPLY_THEBLOCKCHAINXDEV_COM;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import collaborate.api.mail.MailDTO;
import collaborate.api.mail.MailService;
import collaborate.api.nft.create.AssetDTO;
import collaborate.api.nft.create.CreateNFTService;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.UserService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
  private final MailService mailService;
  private final CreateNFTService createNFTService;
  private final UserService userService;
  private final PassportTokenMetadataSupplier passportTokenMetadataSupplier;

  public Job createMultisig(CreateMultisigPassportDTO createMultisigPassportDTO)
      throws IOException {
    String assetOwnerWalletAddress = userService
        .findWalletAddressByEmailOrThrow(createMultisigPassportDTO.getAssetOwnerMail());
    AssetDTO assetDTO = buildAssetDTO(createMultisigPassportDTO);
    var ipfsMetadataUri = createNFTService.saveMetadata(assetDTO, passportTokenMetadataSupplier);
    var job = createPassportDAO.create(
        ipfsMetadataUri,
        assetOwnerWalletAddress,
        createMultisigPassportDTO.getAssetId()
    );
    sendMultisigCreatedEmail(createMultisigPassportDTO.getAssetOwnerMail());
    return job;
  }

  private AssetDTO buildAssetDTO(CreateMultisigPassportDTO createMultisigPassportDTO) {
    return AssetDTO.builder()
        .assetId(createMultisigPassportDTO.getAssetId())
        .assetIdForDatasource(createMultisigPassportDTO.getAssetIdForDatasource())
        .datasourceUUID(createMultisigPassportDTO.getDatasourceUUID())
        .assetType("digital-passport")
        .build();
  }

  void sendMultisigCreatedEmail(String recipient) {
    log.info("SendPassportMail({})", recipient);

    MailDTO mailDTO = new MailDTO(
        NOREPLY_THEBLOCKCHAINXDEV_COM,
        recipient,
        "Your Digital Passport Creation",
        "You received this mail because your passport is created and await your consent");
    log.info("mailDTO={}", mailDTO);

    try {
      mailService.sendMail(mailDTO, StandardCharsets.UTF_8.name(), CONTACT_EMAIL_HTML_TEMPLATE);
    } catch (MessagingException | MailException e) {
      log.error("Problem with Mail sending", e);
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR,
          "Passport was created, but server failed to send mail to Vehicle owner !"
      );
    }
  }

}
