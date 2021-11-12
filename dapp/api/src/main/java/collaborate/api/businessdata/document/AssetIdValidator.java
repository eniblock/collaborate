package collaborate.api.businessdata.document;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class AssetIdValidator implements ConstraintValidator<AssetIdConstraint, String> {

  public static final String ASSET_ID_SEPARATOR = ":";

  @Override
  public boolean isValid(String assetId, ConstraintValidatorContext constraintValidatorContext) {
    if (StringUtils.isBlank(assetId)) {
      return false;
    }
    var assetIdsParts = assetId.split(ASSET_ID_SEPARATOR);
    return StringUtils.isNoneBlank(assetIdsParts) && assetIdsParts.length == 2;
  }
}
