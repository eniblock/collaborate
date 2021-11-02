package collaborate.api.datasource.create;


import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import org.junit.jupiter.api.Test;

class AuthenticationMetadataVisitorTest {

  AuthenticationMetadataVisitor authenticationMetadataVisitor = new AuthenticationMetadataVisitor();

  @Test
  void visitBasicAuth_shouldContainAuthenticationMetadata() {
    // GIVEN
    var basicAuth = new BasicAuth(null, null, null);
    // WHEN
    var metadataResult = authenticationMetadataVisitor.visitBasicAuth(basicAuth)
        .collect(toSet());
    // THEN
    assertThat(metadataResult).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("datasource:authentication")
            .value("BasicAuth")
            .type("string")
            .build()
    );
  }

  @Test
  void visitCertificateBasedBasicAuth_shouldContainAuthenticationAndCaEmailMetadata() {
    // GIVEN
    var certificateBased = CertificateBasedBasicAuth.builder()
        .caEmail("caMail.com")
        .build();
    // WHEN
    var metadataResult = authenticationMetadataVisitor.visitCertificateBasedBasicAuth(
            certificateBased)
        .collect(toSet());
    // THEN
    assertThat(metadataResult).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("datasource:authentication")
            .value("CertificateBasedBasicAuth")
            .type("string")
            .build(),
        Metadata.builder()
            .name("datasource:caEmail")
            .value("caMail.com")
            .type("string")
            .build()
    );
  }

  @Test
  void visitOAuth2_shouldContainAuthenticationMetadata() {
    // GIVEN
    var oAuth2 = new OAuth2();
    // WHEN
    var metadataResult = authenticationMetadataVisitor.visitOAuth2(oAuth2);
    // THEN
    assertThat(metadataResult).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("datasource:authentication")
            .value("OAuth2")
            .type("string")
            .build()
    );
  }
}
