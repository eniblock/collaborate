package collaborate.api.user;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

public class UserFeature {

  public static void mockTagAdminUser(ClientAndServer mockServer) {
    mockServer.when(
        request()
            .withMethod("GET")
            .withPath("/api/user")
            .withQueryStringParameter("userIdList", "admin")
    ).respond(
        response()
            .withStatusCode(200)
            .withHeaders(
                new Header("Content-Type", "application/json; charset=utf-8")
            )
            .withBody("[{\n"
                + "        \"userId\": \"admin\",\n"
                + "        \"account\": \"tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV\"\n"
                + "}]")
    );
  }

}
