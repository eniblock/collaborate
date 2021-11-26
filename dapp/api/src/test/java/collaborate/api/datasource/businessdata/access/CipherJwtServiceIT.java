package collaborate.api.datasource.businessdata.access;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.security.AesCipherService;
import collaborate.api.security.CipherConfig;
import collaborate.api.security.RsaCipherService;
import collaborate.api.security.RsaFeatures;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        CipherConfig.class,
        AesCipherService.class,
        RsaCipherService.class,
        CipherJwtService.class,
    })
class CipherJwtServiceIT {

  @MockBean
  OrganizationService organizationService;
  @MockBean
  ApiProperties apiProperties;

  @Autowired
  CipherJwtService cipherJwtService;

  @Test
  void cipherThenUncipher_shouldBeIdentity() {
    // GIVEN
    String requester = "requester";
    when(organizationService.getByWalletAddress(requester))
        .thenReturn(OrganizationDTO.builder().encryptionKey(RsaFeatures.PUBLIC_KEY).build());
    when(apiProperties.getPrivateKey()).thenReturn(RsaFeatures.PRIVATE_KEY);

    String toCipher = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICItOFdNX2ZXNUtTNHItXzZraDVvSkl5Y19iY3R5WjRVNXA2TUFHNzA4UnBFIn0.eyJleHAiOjE2MzY1MTA5MDUsImlhdCI6MTYzNjQ3NDkwNSwianRpIjoiYmZkYmUyN2QtYTBjMi00MWExLTliYmQtNTA0Mjc4MjBjMjdkIiwiaXNzIjoiaHR0cHM6Ly9mYWtlLWRhdGFzb3VyY2UubG9jYWxob3N0L2F1dGgvcmVhbG1zL2RhdGFzb3VyY2UiLCJzdWIiOiIxNzViZTBjMi1jYWM1LTQ1NjUtYmEwMi05YzJkNjljNzcwNWQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjb2xsYWJvcmF0ZSIsInNlc3Npb25fc3RhdGUiOiIxMmI4MzczZC0wYTZhLTRlZTYtYjQ2ZC02NzViNDA1MGIzMDYiLCJhY3IiOiIxIiwic2NvcGUiOiJtZXRhZGF0YSBwcm9maWxlIGVtYWlsIiwiY2xpZW50SG9zdCI6IjEwLjI0NC4wLjIwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJjbGllbnRJZCI6ImNvbGxhYm9yYXRlIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWNvbGxhYm9yYXRlIiwiY2xpZW50QWRkcmVzcyI6IjEwLjI0NC4wLjIwIn0.OSKe_o8PiuSaL0AlV49BIlk9iLKiTE0MyEMJTqErotqHZy2NIfq8mOXxoJ9jgcx5hmv8UGtd0dStM9p2EqgmQSauoFuAwmdIih1pW0IS_3uITigPukenQtrmH2nqrICIhVW9eAM3PPdVnGd0fJEbPlszPTlmXoMBdTWq8h47MMsCtvV47HC2e5yj1-qBCSdTdeOSiMqj8OEBk0DV6i6vBAgzGPeUv_aHIXr4S5vg9zu3tCYoz7UxanSARx7m2zJC-kh78bH2EfQ6n3uF7xtoQ0q1GRyrqiM_r_79nmwRY9lFfzgyeCMnEhYX1XkXXUMR5YIZwHQQg7BvF8rCWVvoAA";
    // WHEN
    var ciphered = cipherJwtService.cipher(toCipher, requester);
    var decipheredResult = cipherJwtService.decipher(ciphered);
    // THEN
    assertThat(decipheredResult).isEqualTo(toCipher);
  }
}
