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
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngineWrapper templateEngine;

    public void sendMail(
            MailDTO mailDto,
            String templateEncoding,
            String templateName
    ) throws MessagingException {
        final Context context = new Context();
        context.setVariable("greeting", mailDto.getGreeting());
        context.setVariable("content", mailDto.getContent());
        context.setVariable("subject", mailDto.getSubject());

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, templateEncoding);
        final String htmlContent = templateEngine.process(templateName, context);

        message.setText(htmlContent, true);
        message.setFrom(mailDto.getFrom());
        message.setTo(mailDto.getTo());
        message.setSubject(mailDto.getSubject());
        javaMailSender.send(mimeMessage);
    }
}
