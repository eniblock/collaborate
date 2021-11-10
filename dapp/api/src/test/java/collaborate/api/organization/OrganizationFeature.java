package collaborate.api.organization;

import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.organization.model.OrganizationRole;
import collaborate.api.test.TestResources;
import java.util.List;
import lombok.Data;


@Data
public class OrganizationFeature {

  public static final String organizationTagResponseJson =
      TestResources.readPath("/organizations/smartcontract/organization.storage.json");

  public static final OrganizationDTO dspConsortium1Organization = new OrganizationDTO(
      "DSPConsortium1",
      "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV",
      "edpkv2qoSugVizsZRt9dCb2v4iizRhZEQkw2PF5JyZUgHyE6Bp9Yv2",
      List.of(OrganizationRole.DSP)
  );
  public static final OrganizationDTO bspConsortium2Organization = new OrganizationDTO(
      "BSPConsortium2",
      "tz1QdgwqsVV7SmpFPrWjs9B5oBNcj2brzqfG",
      "edpkunY3jsNhGnP3mkYnWmBTYxqjSii1pyY9oUSkNnix3pNHRTMaAc",
      List.of(OrganizationRole.BSP)
  );

}
