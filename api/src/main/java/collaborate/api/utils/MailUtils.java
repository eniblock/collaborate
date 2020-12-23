package collaborate.api.utils;

import collaborate.api.services.dto.MailDTO;
import collaborate.api.wrapper.TemplateEngineWrapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MailUtils {
    public static void sendMail(
            MailDTO maiDto,
            String templateEncoding,
            String templateName,
            JavaMailSender javaMailSender,
            TemplateEngineWrapper templateEngine
    ) throws MessagingException {
        final Context context = new Context();
        context.setVariable("greeting", maiDto.getGreeting());
        context.setVariable("content", maiDto.getContent());
        context.setVariable("subject", maiDto.getSubject());
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, templateEncoding);
        final String htmlContent = templateEngine.process(templateName, context);
        message.setText(htmlContent, true);
        message.setFrom(maiDto.getFrom());
        message.setTo(maiDto.getTo());
        message.setSubject(maiDto.getSubject());
        javaMailSender.send(mimeMessage);
    }
}
