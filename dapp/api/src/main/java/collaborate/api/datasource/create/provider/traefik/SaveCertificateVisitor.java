package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.security.PfxUnProtector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
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
      throw new RuntimeException(String
          .format("While un-protecting certificateBasedBasicAuth=%s", certificateBasedBasicAuth));
    }
    return null;
  }

  @Override
  public Void visitOAuth2(OAuth2 oAuth2) {
    log.error("Not implemented");
    throw new NotImplementedException("OAuth2");
  }
}
