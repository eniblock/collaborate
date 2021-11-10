package collaborate.api.businessdata.access.grant;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import collaborate.api.businessdata.access.request.model.AccessRequest;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.test.config.FeignTestConfig;
import collaborate.api.test.config.KeycloakTestConfig;
import collaborate.api.test.config.NoSecurityTestConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        FeignTestConfig.class,
        KeycloakTestConfig.class,
        NoSecurityTestConfig.class,
        ApiProperties.class,
        TransactionBatchFactory.class,
        GrantAccessDAO.class,
    })
class GrantAccessDAOIT {

  @Autowired
  GrantAccessDAO grantAccessDAO;

  private static ClientAndServer mockServer;

  @BeforeAll
  static void startServer() {
    mockServer = startClientAndServer(1084);
  }

  @AfterAll
  static void stopServer() {
    mockServer.stop();
  }

  public static void mockExistingUUID(ClientAndServer mockServer) {
    mockServer.when(
        request()
            .withMethod("POST")
            .withPath("/api/tezos_node/storage/KT19juhbuKpBmcpccwDMY7WasLo7rSnmRE47")
            .withBody(
                JsonBody.json(
                    readContent(
                        "/businessdata/access/grant/fineOneAccessRequestByUUID-body-existing.json")
                )
            )
    ).respond(
        response()
            .withStatusCode(200)
            .withHeaders(
                new Header("Content-Type", "application/json; charset=utf-8")
            )
            .withBody(
                JsonBody.json(
                    readContent(
                        "/businessdata/access/grant/fineOneAccessRequestByUUID-exist-result.json")
                )
            )
    );
  }

  public static void mockUnexistingUUID(ClientAndServer mockServer) {
    mockServer.when(
        request()
            .withMethod("POST")
            .withPath("/api/tezos_node/storage/KT19juhbuKpBmcpccwDMY7WasLo7rSnmRE47")
            .withBody(
                JsonBody.json(
                    readContent(
                        "/businessdata/access/grant/fineOneAccessRequestByUUID-body-unexisting.json")
                )
            )
    ).respond(
        response()
            .withStatusCode(200)
            .withHeaders(
                new Header("Content-Type", "application/json; charset=utf-8")
            )
            .withBody(
                JsonBody.json(
                    readContent(
                        "/businessdata/access/grant/fineOneAccessRequestByUUID-unexist-result.json")
                )
            )
    );
  }


  @Test
  void findOneAccessRequestById_shouldReturnExpectedAccessRequest_whenExists() {
    // GIVEN
    UUID id = UUID.fromString("05b108f5-1f4c-448b-bf97-8571a2a1e5dc");
    GrantAccessDAOIT.mockExistingUUID(mockServer);
    // WHEN
    var accessRequestResult = grantAccessDAO.findOneAccessRequestById(id);
    // THEN
    assertThat(accessRequestResult)
        .isPresent()
        .hasValue(AccessRequest.builder()
            .accessGranted(true)
            .jwtToken("fake-hash")
            .tokenId(3)
            .providerAddress("tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
            .requesterAddress("tz1QdgwqsVV7SmpFPrWjs9B5oBNcj2brzqfG")
            .scopes(List.of("f40a829f-84d9-4dda-8ea1-3fe2fbe15635:customers-analytics"))
            .build()
        );
  }

  @Test
  void findOneAccessRequestById_shouldReturnExpectedAccessRequest_whenUnexisting() {
    // GIVEN
    UUID id = UUID.fromString("6e83b74f-3a13-4591-839a-3b136f290f79");
    GrantAccessDAOIT.mockUnexistingUUID(mockServer);
    // WHEN
    var accessRequestResult = grantAccessDAO.findOneAccessRequestById(id);
    // THEN
    assertThat(accessRequestResult).isEmpty();
  }
}
