package collaborate.api.organization.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OrganizationRoleConverter implements
    AttributeConverter<List<OrganizationRole>, String> {

  @Override
  public String convertToDatabaseColumn(List<OrganizationRole> organizationRoles) {
    return Optional.ofNullable(organizationRoles)
        .map(roles -> roles.stream()
            .map(OrganizationRole::getCode)
            .map(String::valueOf)
            .collect(Collectors.joining(","))
        )
        .orElse("");
  }

  @Override
  public List<OrganizationRole> convertToEntityAttribute(String roleIds) {
    return Arrays.stream(roleIds.split(","))
        .map(Integer::valueOf)
        .map(OrganizationRole::forValues)
        .collect(Collectors.toList());
  }

}