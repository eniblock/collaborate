package collaborate.api.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class EMailService {

  public static final String NOREPLY_THEBLOCKCHAINXDEV_COM = "noreply@theblockchainxdev.com";

  private final JavaMailSender javaMailSender;
  private final TemplateEngineWrapper templateEngine;

  public void sendMail(
      EMailDTO eMailDTO,
      String templateEncoding,
      String templateName
  ) throws MessagingException {
    log.debug("Sending email: {}", eMailDTO);
    final Context context = new Context();
    eMailDTO.getContextVariables().forEach(context::setVariable);

    final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, templateEncoding);
    final String htmlContent = templateEngine.process(templateName, context);

    message.setText(htmlContent, true);
    message.setFrom(eMailDTO.getFrom());
    message.setTo(eMailDTO.getTo());
    message.setSubject(eMailDTO.getSubject());
    javaMailSender.send(mimeMessage);
  }
}
