package collaborate.api.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail", ignoreUnknownFields = false)
public class MailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties = new Properties();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public static class Properties {
        private Mail mail = new Mail();
        private String addressFrom;

        public Mail getMail() {
            return mail;
        }

        public void setMail(Mail mail) {
            this.mail = mail;
        }

        public String getAddressFrom() {
            return addressFrom;
        }

        public void setAddressFrom(String addressFrom) {
            this.addressFrom = addressFrom;
        }

        public static class Mail {
            private Smtp smtp = new Smtp();

            public Smtp getSmtp() {
                return smtp;
            }

            public void setSmtp(Smtp smtp) {
                this.smtp = smtp;
            }

            public static class Smtp {
                private boolean auth;

                public boolean isAuth() {
                    return auth;
                }

                public void setAuth(boolean auth) {
                    this.auth = auth;
                }
            }
        }
    }
}
