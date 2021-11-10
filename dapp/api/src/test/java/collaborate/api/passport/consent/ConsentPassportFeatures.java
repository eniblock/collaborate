package collaborate.api.passport.consent;

import collaborate.api.tag.model.job.TransactionBatch;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.core.type.TypeReference;

public class ConsentPassportFeatures {

  private ConsentPassportFeatures() {
  }

  public static final String consentPassportTransactionJson = TestResources
      .readPath("/passport/consent/sc.entrypoint.passportConsent.transaction.json");

  public static final TransactionBatch<Integer> consentPassportTransactionBatch =
      TestResources.readValue(
          consentPassportTransactionJson,
          new TypeReference<>() {
          }
      );
}
