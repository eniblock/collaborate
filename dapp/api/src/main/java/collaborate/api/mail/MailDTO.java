package collaborate.api.mail;

import lombok.Data;

@Data
public class MailDTO {

    private String from;
    private String to;
    private String subject;
    private String content;
    private String greeting;

    public MailDTO(String from, String to, String subject, String content) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.greeting = "Hello,";
    }

    public MailDTO(String from, String to, String subject, String content, String greeting) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.greeting = greeting;
    }
}