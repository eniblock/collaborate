package collaborate.api.datasource.gateway;

import collaborate.api.datasource.model.dto.BasicAuthCredentials;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.user.UserService;
import collaborate.api.user.metadata.UserMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SaveAuthenticationVisitor implements AuthenticationVisitor<Void> {

  private final ModelMapper modelMapper;
  private final UserService userService;
  private final UserMetadataService userMetadataService;

  @Override
  public Void visitBasicAuth(BasicAuth basicAuth) {
    var datasourceBasicAuthDto = modelMapper.map(basicAuth, BasicAuthCredentials.class);
    var datasourceId = basicAuth.getDatasource().getId().toString();
    VaultMetadata vaultMetadata = VaultMetadata.builder()
        .basicAuthCredentials(datasourceBasicAuthDto)
        .build();
    upsertMetadata(datasourceId, vaultMetadata);
    return null;
  }

  private void upsertMetadata(String datasourceId, VaultMetadata vaultMetadata) {
    userService.createUser(datasourceId);
    userMetadataService.upsertMetadata(datasourceId, vaultMetadata);
  }

  @Override
  public Void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    visitBasicAuth(basicAuth);
    return null;
  }

  @Override
  public Void visitOAuth2(OAuth2ClientCredentialsGrant oAuth2) {
    VaultMetadata vaultMetadata = VaultMetadata.builder()
        .oAuth2(oAuth2)
        .build();
    upsertMetadata(oAuth2.getDatasource().getId().toString(), vaultMetadata);
    return null;
  }
}
