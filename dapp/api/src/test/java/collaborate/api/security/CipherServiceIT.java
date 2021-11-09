package collaborate.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * RAS Keys can be generated with the followwing commmands<br>
 * <ul>
 *   <li><code>openssl genrsa -out keypair.pem 2048</code></li>
 *   <li>PKCS8 public key:<code>openssl rsa -in keypair.pem -pubout -out publickey.crt</code></li>
 *   <li>PKCS8 private key<code>openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out pkcs8.key</code></li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles({"default", "test"})
@ContextConfiguration(
    classes = {
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        CipherConfig.class,
        CipherService.class})
class CipherServiceIT {

  @Autowired
  CipherService cipherService;

  @Test
  void cipherThenUncipher_shouldBeIdentity()
      throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException {

    var secret = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICItOFdNX2ZXNUtTNHItXzZraDVvSkl5Y19iY3R5WjRVNXA2TUFHNzA4UnBFIn0.eyJleHAiOjE2MzY1MDI4MjksImlhdCI6MTYzNjQ2NjgyOSwianRpIjoiMGFjYjhkODUtNzYwMS00NjZiLWI5ZWUtYjNlMTJhNjRlMWJkIiwiaXNzIjoiaHR0cHM6Ly9mYWtlLWRhdGFzb3VyY2UubG9jYWxob3N0L2F1dGgvcmVhbG1zL2RhdGFzb3VyY2UiLCJzdWIiOiIxNzViZTBjMi1jYWM1LTQ1NjUtYmEwMi05YzJkNjljNzcwNWQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjb2xsYWJvcmF0ZSIsInNlc3Npb25fc3RhdGUiOiJjYjRkOTQ3Mi1iNGVmLTRhMDAtYWQxNy03YmUzNjNjMDUwYjUiLCJhY3IiOiIxIiwic2NvcGUiOiJtZXRhZGF0YSBjdXN0b21lcnMtYW5hbHl0aWNzIHByb2ZpbGUgZW1haWwiLCJjbGllbnRIb3N0IjoiMTAuMjQ0LjAuMjAiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImNsaWVudElkIjoiY29sbGFib3JhdGUiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQtY29sbGFib3JhdGUiLCJjbGllbnRBZGRyZXNzIjoiMTAuMjQ0LjAuMjAifQ.X1M5KaJGVT5KAwuezbTtQqM5OMs_SqCbvikHrDGsDrorB6MLaPA5_OQMe031BzExzwo2_DKJkbtdrgoEBiZDiHoGhYkmCez_agehwHO_yY-cVaor-VQe4cNie_7n-Se4xfzGr8s_StQsLWhDYcRK_qhFnhTumlhx0F-7e7YlNJzp_SynRRWu8rydstczCGiRmRGR9lZ_F17B3hEzy399BBpsTLnjm4btcO1swqPCF-DjrL8zDmu0r1vuYmU48dQhIn9tPlZZA2YiCPB2QH_0Y3aN8fQhp7FQqcqaMSUpG_m_AJo-OOwX6AiKGZ7uiHheN36eVX4Bm3WduP2TxeGiKQ";
    String rawPublicKeyA = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1ErK7tNhyN82kcnEJHX2f8sgZsK9xMj5EYmYRE8PU2cpBnZ0li1x+ZrF3vXvdWYi6FL0WleWlDhD6iLys7OtK15fR0n5ZklmwthFmSUvFflLd04f+UJW8ovbUpxq3mGJVphNRiIyrUInlCsRhwelqFypBdT++yGAPs3k48r9AeBgssNmirYcpt0rkYVybbBNGmxD5rh0vkb1wbVehTZ/r5Lq/u/sh63oBuBWq+XBSqDIAgroFwlDypEzxnUsghCbise/3QVTuIOd51BkoEaCxd4Q2hHIGjBVPI2lMyxpaX4v5hUQAAGz8uAfFib5zNSP4iApFdk67i0Z3LyHhTeq6wIDAQABMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1ErK7tNhyN82kcnEJHX2f8sgZsK9xMj5EYmYRE8PU2cpBnZ0li1x+ZrF3vXvdWYi6FL0WleWlDhD6iLys7OtK15fR0n5ZklmwthFmSUvFflLd04f+UJW8ovbUpxq3mGJVphNRiIyrUInlCsRhwelqFypBdT++yGAPs3k48r9AeBgssNmirYcpt0rkYVybbBNGmxD5rh0vkb1wbVehTZ/r5Lq/u/sh63oBuBWq+XBSqDIAgroFwlDypEzxnUsghCbise/3QVTuIOd51BkoEaCxd4Q2hHIGjBVPI2lMyxpaX4v5hUQAAGz8uAfFib5zNSP4iApFdk67i0Z3LyHhTeq6wIDAQAB";
    String rawPrivateKeyA = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDUSsru02HI3zaRycQkdfZ/yyBmwr3EyPkRiZhETw9TZykGdnSWLXH5msXe9e91ZiLoUvRaV5aUOEPqIvKzs60rXl9HSflmSWbC2EWZJS8V+Ut3Th/5Qlbyi9tSnGreYYlWmE1GIjKtQieUKxGHB6WoXKkF1P77IYA+zeTjyv0B4GCyw2aKthym3SuRhXJtsE0abEPmuHS+RvXBtV6FNn+vkur+7+yHregG4Far5cFKoMgCCugXCUPKkTPGdSyCEJuKx7/dBVO4g53nUGSgRoLF3hDaEcgaMFU8jaUzLGlpfi/mFRAAAbPy4B8WJvnM1I/iICkV2TruLRncvIeFN6rrAgMBAAECggEBALIUN1zVhp5Y1869oMIxDeCvRPPOgh+TspLKiCcs5p9A1wIr1aqwXn5SHY/lX8RsM/YMuBschBy7ggTi4hJqM2ZA2M3VONdb1U622tMXhQL6xxpCN8IAqyz0srr+qQr4aJtyUMf3IemCp+67ng1WFdlAOSzCOk3xEUIdttZ6zXeG+I5L9GCmFVMZZC3pNRmAV76B58h5yVNzXrgWwCIfd7cAseNExueL04qxWN/+RHDINyTKL5kVjkly8ZxFiCpYC2OErfuLXQCBze2YhSKiuxvn+qipQSw001tqpVO/+O8urYnVClLFhg0IrRj21F6S6bu/kjW/dZGETHdwBZQe41ECgYEA7KpzP73gooZT94YHMn7dIfm4vgP63rFsmRetcrLw1UHoqK1Hw7EeymvPnNxZ3cqgpHv0CxsUN8Wy3d6lL3t6stwaPBE8j1fJWO/kHrDD/meLEVtUINLg6a6rdRYxZpRbg7lseH592yDE7Ylj63WU+9Kt1CSFDOwEpCZBrklyb9kCgYEA5aKZkx2uQ3A7PEpaBVbolVQJtjhXYfR8b7Gefkm3pcnEbOPtyP5CIs4HQsaKL91HnT1WNLur2N7GozWy+a/+dPYvZ8ox2Md/E5g+S2gMU9fTT0nlvlAbTzjTL3RsAtdVzwSTcylbFohqcwdi2216WnMe/LAqP+nVDx+ElxlRemMCgYA7gyE4BidWtzCmLa3hpMU2pfUu0xFirnsAhFO5ZAXrmodG40wIiaGUhUfPyhlPDIUY15KmuEh9PdmcsUAYkJRqJa7zK+JuqUSYeLqEfS7L6n2t/6lIF783BBVZfBswEF5y4U3Ffwlm8PaURNg7fXXZXUEY95X9idwg6v3ZNd7xAQKBgFy4YMY0L87vHBvl8Z9CTjstaZZ3/WCB9VSsJVDctxhr9bH5/mMvtsiS4/+kZtuYv3fP8mTseiDhItd3sUJ8O4RCezWvQJ1FQBxLVqvf4UphVOMo9Wj4s24zhaIdohcrl2w1bswLIMpiXoj/rHzpIYQwA40IHKcd5XdrxH+2K139AoGAMCYj9JaNOFWBuNMzv2KkrdnFXZig/yp3AnvMqUqJsr+mspmotOFhdAlf8oHGGwdQNLE26SkCO/W3u46nUziIMXOhJZ24+pe9ICRzFuuYt2eME0nLPWq36fIE0h3Eejpp/YJm8NdYju3xQGuw9nhBVqb3C+1TFHSg0u81r4aP6cs=";
    // WHEN
    var ciphered = cipherService.cipher(secret, rawPublicKeyA);
    var deciphered = cipherService.decipher(ciphered, rawPrivateKeyA);
    // THEN
    assertThat(deciphered).isEqualTo(secret);
  }
}
