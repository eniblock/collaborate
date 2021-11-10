package collaborate.api.datasource.security;

import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.tag.model.user.UserMetadataDTO;
import collaborate.api.user.UserService;
import collaborate.api.user.tag.TezosApiGatewayUserClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SaveAuthenticationVisitor implements AuthenticationVisitor<Void> {

  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;
  private final UserService userService;
  private final TezosApiGatewayUserClient tagUserClient;

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
    tagUserClient.upsertMetadata(datasourceId, serialize(vaultMetadata));
  }

  private UserMetadataDTO serialize(VaultMetadata vaultMetadata) {
    try {
      return new UserMetadataDTO(objectMapper.writeValueAsString(vaultMetadata));
    } catch (JsonProcessingException e) {
      log.error("can't serialized vaultMetadata={}", vaultMetadata);
      throw new IllegalStateException("Can't write vaultMetadata");
    }
  }

  @Override
  public Void visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    visitBasicAuth(basicAuth);
    return null;
  }

  @Override
  public Void visitOAuth2(OAuth2 oAuth2) {
    VaultMetadata vaultMetadata = VaultMetadata.builder()
        .oAuth2(oAuth2)
        .build();
    upsertMetadata(oAuth2.getDatasource().getId().toString(), vaultMetadata);
    return null;
  }
}
