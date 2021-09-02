package collaborate.api.datasource.traefik;

import collaborate.api.datasource.domain.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.domain.web.authentication.BasicAuth;
import collaborate.api.datasource.domain.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.domain.web.authentication.OAuth2;
import collaborate.api.security.PfxUnProtector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;

@RequiredArgsConstructor
@Slf4j
public class SaveCertificateVisitor implements AuthenticationVisitor {

  private final PfxUnProtector pfxUnprotector;
  private final String output;

  @Override
  public void visitBasicAuth(BasicAuth basicAuth) {
    // No-ops
  }

  @Override
  public void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth certificateBasedBasicAuth)
      throws Exception {
    visitBasicAuth(certificateBasedBasicAuth);
    try {
      log.info("pfxUnProtector exitCode={}", pfxUnprotector.unprotect(certificateBasedBasicAuth, output));
    } catch (Exception e) {
      log.error("While un-protecting certificateBasedBasicAuth={}", certificateBasedBasicAuth);
      throw e;
    }
  }


  @Override
  public void visitOAuth2(OAuth2 oAuth2) {
    log.error("Not implemented");
    throw new NotImplementedException("OAuth2");
  }
}
