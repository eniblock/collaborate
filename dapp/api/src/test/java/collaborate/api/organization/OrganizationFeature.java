package collaborate.api.organization;

import static collaborate.api.test.TestResources.readContent;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.organization.model.OrganizationRole;
import collaborate.api.test.TestResources;
import java.util.List;
import lombok.Data;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;


@Data
public class OrganizationFeature {

  public static final String organizationTagResponseJson =
      TestResources.readContent("/organizations/smartcontract/organization.storage.json");

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

  public static void mockTagOrganizationsStorage(ClientAndServer mockServer) {
    mockServer.when(
        request()
            .withMethod("POST")
            .withPath("/api/tezos_node/storage/KT1VzQ7jB8hXSk5iddUtGoraXx8Cd329ftJE")
    ).respond(
        response()
            .withStatusCode(200)
            .withHeaders(
                new Header("Content-Type", "application/json; charset=utf-8")
            )
            .withBody(readContent("/organization/organization-sc-storage.json"))
    );
  }
}
