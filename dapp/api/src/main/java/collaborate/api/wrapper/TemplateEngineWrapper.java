package collaborate.api.wrapper;

import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

/**
 * Wrapper around templateEngine calls.
 * This class is used to permit unit tests since all TemplateEngine method are final
 */
@Component
public class TemplateEngineWrapper {

    // See MailConfig for been definition
    private final ITemplateEngine emailTemplateEngine;

    public TemplateEngineWrapper(ITemplateEngine emailTemplateEngine) {
        this.emailTemplateEngine = emailTemplateEngine;
    }

    public String process(String templateName, IContext context) {
        return emailTemplateEngine.process(templateName, context);
    }

}
