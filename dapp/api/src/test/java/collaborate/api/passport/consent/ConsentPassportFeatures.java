package collaborate.api.passport.consent;

import collaborate.api.tag.model.job.TransactionBatch;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.core.type.TypeReference;

public class ConsentPassportFeatures {

  private ConsentPassportFeatures() {
  }

  public static final String consentPassportTransactionJson = TestResources
      .read("/passport/consent/sc.entrypoint.passportConsent.transaction.json");

  public static final TransactionBatch<PassportConsentEntryPointParam> consentPassportTransactionBatch =
      TestResources.read(
          consentPassportTransactionJson,
          new TypeReference<>() {
          }
      );
}
