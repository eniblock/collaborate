package collaborate.api.http.security;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Slf4j
public class SSLContextCreator {

  public SSLContext create(byte[] pfxFileContent, char[] password)
      throws SSLContextException, UnrecoverableKeyException {
    var keyStore = buildKeyStore(pfxFileContent, password);
    var keyManagerFactory = buildKeyManagerFactory(keyStore, password);
    return buildSslContext(keyManagerFactory);
  }

  @NotNull
  KeyStore buildKeyStore(byte[] pfxFileContent, char[] password)
      throws SSLContextException, UnrecoverableKeyException {
    KeyStore keyStore;
    var keyStoreAlgorithm = "PKCS12";
    try {
      keyStore = KeyStore.getInstance(keyStoreAlgorithm);
    } catch (KeyStoreException e) {
      var errorMessage = "Can't get a KeyStore instance of type={" + keyStoreAlgorithm + "}";
      log.error(errorMessage, e);
      throw new SSLContextException(errorMessage, e);
    }

    var inputStream = new ByteArrayInputStream(pfxFileContent);
    try {
      keyStore.load(inputStream, password);
    } catch (Exception e) {
      var errorMessage = "Can't load pfxFileContent to keyStore";
      log.error(errorMessage, e);
      if(e.getCause() instanceof UnrecoverableKeyException){
        throw (UnrecoverableKeyException) e.getCause();
      }else {
        throw new SSLContextException(errorMessage, e);
      }
    }
    return keyStore;
  }

  KeyManagerFactory buildKeyManagerFactory(KeyStore keyStore, char[] password)
      throws SSLContextException {
    KeyManagerFactory keyManagerFactory;
    var keyManagerAlgorithm = "SunX509";
    try {
      keyManagerFactory = KeyManagerFactory.getInstance(keyManagerAlgorithm);
    } catch (NoSuchAlgorithmException e) {
      var errorMessage =
          "Can't get a KeyManagerFactory instance of type={" + keyManagerAlgorithm + "}";
      log.error(errorMessage, e);
      throw new SSLContextException(errorMessage, e);
    }
    try {
      keyManagerFactory.init(keyStore, password);
    } catch (Exception e) {
      var errorMessage = "Can't init KeyManagerFactory with the given keyStore and password";
      log.error(errorMessage, e);
      throw new SSLContextException(errorMessage, e);
    }
    return keyManagerFactory;
  }

  SSLContext buildSslContext(KeyManagerFactory keyManagerFactory) throws SSLContextException {
    var sslProtocol = "TLS";
    SSLContext sslContext;
    try {
      sslContext = SSLContext.getInstance(sslProtocol);
    } catch (NoSuchAlgorithmException e) {
      var errorMessage = "Can't get an instance of SSLContext for protocol={" + sslProtocol + "}";
      log.error(errorMessage, e);
      throw new SSLContextException(errorMessage, e);
    }
    try {
      sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
    } catch (KeyManagementException e) {
      var errorMessage = "Can't init sslContext";
      log.error(errorMessage, e);
      throw new SSLContextException(errorMessage, e);
    }
    return sslContext;
  }
}
