package collaborate.api.organization.model;

import collaborate.api.organization.OrganizationService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LegalNameNotUsedValidator implements
    ConstraintValidator<LegalNameNotUsedConstraint, String> {

  private final OrganizationService organizationService;

  @Override
  public boolean isValid(String legalName, ConstraintValidatorContext context) {
    return organizationService
        .findByLegalNameIgnoreCase(legalName)
        .isEmpty();
  }
}