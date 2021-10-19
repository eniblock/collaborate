package collaborate.api.datasource.security;

import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.security.VaultService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SaveAuthenticationVisitor implements AuthenticationVisitor<Void> {

  private final VaultService vaultService;
  private final VaultKeyFactory vaultKeyFactory;
  private final ModelMapper modelMapper;

  @Override
  public Void visitBasicAuth(BasicAuth basicAuth) {
    var datasourceBasicAuthDto = modelMapper.map(basicAuth, BasicAuthCredentials.class);

    var vaultKey = vaultKeyFactory.createBasicAuth(basicAuth.getDatasource().getId());
    vaultService.put(vaultKey, datasourceBasicAuthDto);
    return null;
  }

  @Override
  public Void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    visitBasicAuth(basicAuth);
    return null;
  }

  @Override
  public Void visitOAuth2(OAuth2 oAuth2) {
    OAuth2ClientSecret oAuth2ClientSecret = OAuth2ClientSecret.builder()
        .clientId(oAuth2.getClientId())
        .clientSecret(oAuth2.getClientSecret())
        .build();

    var vaultKey = vaultKeyFactory.createOAuth2(oAuth2.getDatasource().getId());
    vaultService.put(vaultKey, oAuth2ClientSecret);
    return null;
  }
}
