package collaborate.api.organization;

import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.test.TestResources;
import lombok.Data;


@Data
public class OrganizationFeature {

  public static final String organizationTagResponseJson =
      TestResources.read("/organizations/smartcontract/organization.storage.json");

  public static final OrganizationDTO psaOrganization = new OrganizationDTO(
      "psa",
      "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV",
      "edpkv2qoSugVizsZRt9dCb2v4iizRhZEQkw2PF5JyZUgHyE6Bp9Yv2"
  );
  public static final OrganizationDTO movidiaOrganization = new OrganizationDTO(
      "mobivia",
      "tz1QdgwqsVV7SmpFPrWjs9B5oBNcj2brzqfG",
      "edpkunY3jsNhGnP3mkYnWmBTYxqjSii1pyY9oUSkNnix3pNHRTMaAc"
  );

}
