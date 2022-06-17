package collaborate.api.security;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

/**
 * PfxUnprotector Script:<br> Required parameters:<br> - $1: encrypted pfx file<br> - $2: pfx file
 * password<br> - $3: output without extension<br>
 */
@Component
@RequiredArgsConstructor
@Log
public class PfxUnProtector {

  private final TraefikProperties traefikProperties;

  public int unprotect(CertificateBasedBasicAuth certificateBasedBasicAuth)
      throws IOException, InterruptedException {

    var pfxFilePath = writePfxFile(certificateBasedBasicAuth);
    Process process = buildProcess(certificateBasedBasicAuth, pfxFilePath);
    Executors.newSingleThreadExecutor()
        .submit(new StreamGobbler(
                process.getInputStream(),
                s -> log.log(log.getLevel(), s)
            )
        );

    try {
      return process.waitFor();
    } finally {
      Files.delete(pfxFilePath);
    }
  }

  private Process buildProcess(CertificateBasedBasicAuth certificateBasedBasicAuth,
      Path pfxFilePath)
      throws IOException {
    String[] executeScriptCommand = new String[]{
        "/bin/bash",
        traefikProperties.getPfxUnProtectorScriptPath(),
        pfxFilePath.toString(),
        certificateBasedBasicAuth.getPassphrase(),
        Paths.get(
            traefikProperties.getCertificatesPath(),
            certificateBasedBasicAuth.getDatasource().getId().toString()).toString()
    };
    log.info("Executing " + Arrays.toString(executeScriptCommand));
    return Runtime.getRuntime()
        .exec(executeScriptCommand);
  }

  private Path writePfxFile(CertificateBasedBasicAuth certificateBasedBasicAuth)
      throws IOException {
    var pfxFilePath = Paths.get(
        traefikProperties.getCertificatesPath(),
        certificateBasedBasicAuth.getDatasource().getId() + ".pfx"
    );
    try {
      log.info("writing pfxFile=" + pfxFilePath);
      Files.write(pfxFilePath, certificateBasedBasicAuth.getPfxFileContent());
    } catch (IOException e) {
      throw new IOException("While writing certificate", e);
    }
    return pfxFilePath;
  }
}
