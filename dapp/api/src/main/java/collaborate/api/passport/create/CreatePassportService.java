package collaborate.api.passport.create;

import static collaborate.api.ipfs.IpfsService.IPFS_PROTOCOL_PREFIX;
import static collaborate.api.mail.MailService.NOREPLY_THEBLOCKCHAINXDEV_COM;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import collaborate.api.ipfs.IpfsDAO;
import collaborate.api.mail.MailDTO;
import collaborate.api.mail.MailService;
import collaborate.api.passport.TokenMetadataProperties;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.UserService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

  private final AssetDataCatalogFactory assetDataCatalogFactory;
  private final CreatePassportDAO createPassportDAO;
  private final IpfsDAO ipfsDAO;
  private final MailService mailService;
  private final UserService userService;
  private final TokenMetadataFactory tokenMetadataFactory;
  private final TokenMetadataProperties tokenMetadataProperties;

  public Job createMultisig(CreateMultisigPassportDTO createMultisigPassportDTO) throws Exception {
    String assetOwnerWalletAddress = userService
        .findWalletAddressByEmailOrThrow(createMultisigPassportDTO.getAssetOwnerMail());
    var ipfsMetadataUri = saveMetadata(createMultisigPassportDTO);
    var job = createPassportDAO.create(
        ipfsMetadataUri,
        assetOwnerWalletAddress,
        createMultisigPassportDTO.getAssetId()
    );
    sendMultisigCreatedEmail(createMultisigPassportDTO.getAssetOwnerMail());
    return job;
  }

  String saveMetadata(CreateMultisigPassportDTO createMultisigPassportDTO)
      throws Exception {
    var assetDataCatalogRelativePath = assetDataCatalogFactory.buildRelativePathForAssetId(
        createMultisigPassportDTO.getAssetId()
    ).toString();

    saveAssetDataCatalog(createMultisigPassportDTO, assetDataCatalogRelativePath);

    return IPFS_PROTOCOL_PREFIX + ipfsDAO.add(
        tokenMetadataFactory.buildPathForAssetId(createMultisigPassportDTO.getAssetId()),
        tokenMetadataFactory.create(createMultisigPassportDTO, assetDataCatalogRelativePath)
    );
  }

  private void saveAssetDataCatalog(CreateMultisigPassportDTO createMultisigPassportDTO,
      String assetDataCatalogRelativePath) throws IOException {
    var assetDataCatalogPath = Path.of(
        tokenMetadataProperties.getAssetDataCatalogRootFolder(),
        assetDataCatalogRelativePath
    );
    ipfsDAO.add(
        assetDataCatalogPath,
        assetDataCatalogFactory.create(createMultisigPassportDTO)
    );
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
