package collaborate.api.datasource.create;


import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.model.dto.web.authentication.transfer.CertificateBasedAuthorityEmail;
import collaborate.api.datasource.model.dto.web.authentication.transfer.OAuth2;
import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;

class AuthenticationMetadataVisitorTest {

  AuthenticationMetadataVisitor authenticationMetadataVisitor = new AuthenticationMetadataVisitor(
      TestResources.objectMapper
  );

  @Test
  void visitBasicAuth_shouldContainAuthenticationMetadata() {
    // GIVEN
    var basicAuth = new BasicAuth(new CertificateBasedAuthorityEmail(), null, null, null);
    // WHEN
    var metadataResult = authenticationMetadataVisitor.visitBasicAuth(basicAuth)
        .collect(toSet());
    // THEN
    assertThat(metadataResult).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("datasource:authentication")
            .value("BasicAuth")
            .type("string")
            .build(),
        Metadata.builder()
            .name("datasource:partnerTransferMethod")
            .value("{\"type\":\"CertificateBasedAuthorityEmail\"}")
            .type(CertificateBasedAuthorityEmail.class.getName())
            .build()
    );
  }

  @Test
  void visitCertificateBasedBasicAuth_shouldContainAuthenticationAndCaEmailMetadata() {
    // GIVEN
    var certificateBased = CertificateBasedBasicAuth.builder()
        .partnerTransferMethod(new CertificateBasedAuthorityEmail("caMail.com"))
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
            .name("datasource:partnerTransferMethod")
            .value("{\"type\":\"CertificateBasedAuthorityEmail\",\"email\":\"caMail.com\"}")
            .type(CertificateBasedAuthorityEmail.class.getName())
            .build()
    );
  }

  @Test
  void visitOAuth2_shouldContainAuthenticationMetadata() {
    // GIVEN
    var oAuth2 = new OAuth2ClientCredentialsGrant();
    oAuth2.setPartnerTransferMethod((new OAuth2()));
    // WHEN
    var metadataResult = authenticationMetadataVisitor.visitOAuth2(oAuth2);
    // THEN
    assertThat(metadataResult).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("datasource:authentication")
            .value("OAuth2ClientCredentialsGrant")
            .type("string")
            .build(),
        Metadata.builder()
            .name("datasource:partnerTransferMethod")
            .value("{\"type\":\"OAuth2\"}")
            .type(OAuth2.class.getName())
            .build()
    );
  }
}
