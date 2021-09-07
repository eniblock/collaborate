package collaborate.api.passport.create.rabbitmq;

import static java.lang.String.format;

import collaborate.api.mail.MailDTO;
import collaborate.api.mail.MailService;
import collaborate.api.tag.model.job.TransactionsEventMessage;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.tag.TagUserDAO;
import java.nio.charset.StandardCharsets;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitPassportCreationEventConsumer {

  public static final String NOREPLY_THEBLOCKCHAINXDEV_COM = "noreply@theblockchainxdev.com";
  public static final String CONTACT_EMAIL_HTML_TEMPLATE = "html/contactEmail.html";
  private final MailService mailService;
  private final TagUserDAO tagUserDAO;
  private final RabbitTemplate rabbitTemplate; //Required for RabbitMQ Beans

  @RabbitListener(containerFactory = "tezos-api-gateway", bindings = @QueueBinding(
      value = @Queue(
          arguments = {
              @Argument(name = "exclusive", value = "true")
          }
      ),
      exchange = @Exchange(name = "headers-exchange", type = "headers"),
      arguments = {
          @Argument(name = "entrypoint", value = "initPassportCreation"),
          @Argument(name = "contractAddress", value = "#{contractAddress}"),
          @Argument(name = "x-match", value = "all")
      },
      key = ""))
  void listenPassportCreate(TransactionsEventMessage<InitPassportCreationValue> message)
      throws MessagingException {
    log.info("Received custom message: " + message.toString());

    InitPassportCreationValue messageValue = message.getParameters().getValue();
    String messageOrganisationAddress = messageValue.getInitPassportCreation().getDspAddress();
    String organizationPublicKeyHash = tagUserDAO.getOrganizationAccountAddress();

    if (StringUtils.equals(messageOrganisationAddress, organizationPublicKeyHash)) {
      log.info("Message is about this organisation - sending mail for passport creation");
      sendPassportMail(messageValue.getInitPassportCreation());
    }
  }

  void sendPassportMail(InitPassportCreationParams messageParams) throws MessagingException {
    log.info("SendPassportMail({})", messageParams);

    var recipient = tagUserDAO
        .findOneByAddress(messageParams.getVehicleOwnerAddress())
        .map(UserWalletDTO::getEmail)
        .orElseThrow(() -> new IllegalStateException(
            format("No user found for vehicleOwnerAddress=%s",
                messageParams.getVehicleOwnerAddress())));

    MailDTO mailDTO = new MailDTO(
        NOREPLY_THEBLOCKCHAINXDEV_COM,
        recipient,
        "Your Digital Passport Creation",
        "You receive this mail because your passport is created and await your consent");
    log.info("mailDTO={}", mailDTO);
    mailService.sendMail(mailDTO, StandardCharsets.UTF_8.name(), CONTACT_EMAIL_HTML_TEMPLATE);
  }

}
