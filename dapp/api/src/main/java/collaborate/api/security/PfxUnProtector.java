package collaborate.api.security;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.domain.web.authentication.CertificateBasedBasicAuth;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log
public class PfxUnProtector {

  private final Path workingDirectory;
  private final Path scriptPath;

  public PfxUnProtector(@Autowired ApiProperties apiProperties) {
    this.workingDirectory = apiProperties.getTraefik().getCertificatesPath();
    this.scriptPath = Paths.get(
        apiProperties.getTraefik().getCertificatesPath().toString(),
        "pfx-un-protector.sh"
    );
  }

  public int unprotect(CertificateBasedBasicAuth certificateBasedBasicAuth, String output)
      throws IOException, InterruptedException {

    var pfxFilePath = writePfxFile(certificateBasedBasicAuth, output);
    Process process = buildProcess(certificateBasedBasicAuth, pfxFilePath, output);
    StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(),
        s -> log.log(log.getLevel(), s));
    Executors.newSingleThreadExecutor().submit(streamGobbler);

    try {
      return process.waitFor();
    } finally {
      Files.delete(pfxFilePath);
    }
  }

  private Process buildProcess(CertificateBasedBasicAuth certificateBasedBasicAuth,
      Path pfxFilePath, String output)
      throws IOException {
    String executeScriptCommand = String.format("/bin/bash %s %s %s %s",
        scriptPath,
        pfxFilePath,
        certificateBasedBasicAuth.getPassphrase(),
        Paths.get(
            workingDirectory.toString(),
            output
        ));
    log.info("Executing " + executeScriptCommand);
    return Runtime.getRuntime()
        .exec(
            executeScriptCommand
        );
  }

  private Path writePfxFile(CertificateBasedBasicAuth certificateBasedBasicAuth, String output)
      throws IOException {
    var pfxFilePath = Paths.get(
        workingDirectory.toString(),
        output + ".pfx"
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
