package collaborate.api.datasource.security;

import collaborate.api.datasource.domain.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.domain.web.authentication.BasicAuth;
import collaborate.api.datasource.domain.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.domain.web.authentication.OAuth2;
import collaborate.api.security.VaultService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SaveAuthenticationToDatabaseVisitor implements AuthenticationVisitor {

  private final VaultService vaultService;
  private final VaultKeyFactory vaultKeyFactory;
  private final ModelMapper modelMapper;

  @Override
  public void visitBasicAuth(BasicAuth basicAuth) {
    var datasourceBasicAuthDto = modelMapper.map(basicAuth, BasicAuthCredentials.class);

    var vaultKey = vaultKeyFactory.createBasicAuth(basicAuth.getDatasource().getId());
    vaultService.put(vaultKey, datasourceBasicAuthDto);
  }

  @Override
  public void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    visitBasicAuth(basicAuth);
  }

  @Override
  public void visitOAuth2(OAuth2 oAuth2) {
    OAuth2ClientSecret oAuth2ClientSecret = OAuth2ClientSecret.builder()
        .clientId(oAuth2.getClientId())
        .clientSecret(oAuth2.getClientSecret())
        .build();

    var vaultKey = vaultKeyFactory.createOAuth2(oAuth2.getDatasource().getId());
    vaultService.put(vaultKey, oAuth2ClientSecret);
  }
}
