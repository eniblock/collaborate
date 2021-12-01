package collaborate.api.datasource.gateway.traefik;

import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.security.PfxUnProtector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class SaveCertificateVisitor implements AuthenticationVisitor<Void> {

  private final PfxUnProtector pfxUnprotector;

  @Override
  public Void visitBasicAuth(BasicAuth basicAuth) {
    return null;
  }

  @Override
  public Void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth certificateBasedBasicAuth) {
    visitBasicAuth(certificateBasedBasicAuth);
    try {
      log.info("pfxUnProtector exitCode={}", pfxUnprotector.unprotect(certificateBasedBasicAuth));
    } catch (Exception e) {
      log.error("While un-protecting certificateBasedBasicAuth={}", certificateBasedBasicAuth);
      Thread.currentThread().interrupt();
    }
    return null;
  }

  @Override
  public Void visitOAuth2(OAuth2ClientCredentialsGrant oAuth2) {
    // No-ops
    return null;
  }
}
