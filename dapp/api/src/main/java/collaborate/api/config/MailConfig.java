package collaborate.api.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class MailConfig {

  private static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";
  private static final String TEMPLATERESOLVER_PREFIX = "/mail/";
  private static final String TEMPLATERESOLVER_SUFFIX = ".html";
  private static final String TEMPLATERESOLVER_PATTERN = "html/*";

  @Bean
  public ITemplateEngine emailTemplateEngine() {
    final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.addTemplateResolver(htmlTemplateResolver());
    return templateEngine;
  }

  private ITemplateResolver htmlTemplateResolver() {
    final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setOrder(2);
    templateResolver.setResolvablePatterns(Collections.singleton(TEMPLATERESOLVER_PATTERN));
    templateResolver.setPrefix(TEMPLATERESOLVER_PREFIX);
    templateResolver.setSuffix(TEMPLATERESOLVER_SUFFIX);
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
    templateResolver.setCacheable(false);
    return templateResolver;
  }
}
