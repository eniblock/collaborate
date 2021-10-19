package collaborate.api.ipfs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpdateIpnsKeysScheduler {

  private final IpnsService ipnsService;

  @Scheduled(
      fixedDelayString = "${ipfs.update-ipns-keys.fixed-delay-in-ms}",
      initialDelayString = "${ipfs.update-ipns-keys.initial-delay-in-ms}"
  )
  public void scheduleFixedRateWithInitialDelayTask() {
    log.info("Updating ipns keys");
    ipnsService.updateKeys();
  }
}
