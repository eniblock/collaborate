package collaborate.api.domain.enumeration;

public enum DatasourceAccessMethod {
    OAUTH2_CLIENT_CREDENTIALS_GRANT,
    OAUTH2_AUTHORIZATION_CODE_GRANT,
    OAUTH2_PROOF_KEY_FOR_CODE_EXCHANGE,
    OAUTH2_DEVICE_CODE,
    OAUTH2_REFRESH_TOKEN;
}
