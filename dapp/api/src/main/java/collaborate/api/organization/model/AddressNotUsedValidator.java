package collaborate.api.organization.model;

import collaborate.api.organization.OrganizationService;
import lombok.RequiredArgsConstructor;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class AddressNotUsedValidator implements ConstraintValidator<AddressNotUsedConstraint, String> {
    private final OrganizationService organizationService;

    @Override
    public boolean isValid(String organizationAddress, ConstraintValidatorContext context) {
        return organizationService.findOrganizationByPublicKeyHash(organizationAddress)
            .filter(OrganizationDTO::isActive)
                .isEmpty();
    }
}