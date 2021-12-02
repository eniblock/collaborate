package collaborate.api.datasource.create;

import static lombok.AccessLevel.PRIVATE;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationMetadataVisitor implements AuthenticationVisitor<Stream<Metadata>> {

  private final ObjectMapper objectMapper;

  @NoArgsConstructor(access = PRIVATE)
  public static final class Keys {

    public static final String DATASOURCE_AUTHENTICATION = "datasource:authentication";
    public static final String PARTNER_TRANSFER_METHOD = "datasource:partnerTransferMethod";
  }

  @Override
  public Stream<Metadata> visitBasicAuth(BasicAuth basicAuth) {
    return Stream.of(
        buildAuthentication(basicAuth),
        buildPartnerTransferMethod(basicAuth.getPartnerTransferMethod())
    );
  }

  @Override
  public Stream<Metadata> visitCertificateBasedBasicAuth(
      CertificateBasedBasicAuth certificateBasedBasicAuth) {
    return visitBasicAuth(certificateBasedBasicAuth);
  }

  @Override
  public Stream<Metadata> visitOAuth2(OAuth2ClientCredentialsGrant oAuth2) {
    return Stream.of(buildAuthentication(oAuth2),
        buildPartnerTransferMethod(oAuth2.getPartnerTransferMethod())
    );
  }

  private Metadata buildAuthentication(Authentication authentication) {
    return Metadata.builder()
        .name(Keys.DATASOURCE_AUTHENTICATION)
        .value(authentication.getClass().getSimpleName())
        .type("string")
        .build();
  }

  private Metadata buildPartnerTransferMethod(PartnerTransferMethod partnerTransferMethod) {
    try {
      return Metadata.builder()
          .name(Keys.PARTNER_TRANSFER_METHOD)
          .value(objectMapper.writeValueAsString(partnerTransferMethod))
          .type(partnerTransferMethod.getClass().getName())
          .build();
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Can't serialize PartnerTransferMethod");
    }
  }
}
