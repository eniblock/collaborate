package collaborate.api.config;

import static lombok.AccessLevel.PRIVATE;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.NoArgsConstructor;

@OpenAPIDefinition(info = @Info(title = "Collaborate",
    description = "Collaborate service API"))
@SecurityScheme(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK, type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        implicit = @OAuthFlow(
            authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}"
        ),
        password = @OAuthFlow(
            tokenUrl = "${springdoc.oAuthFlow.tokenUrl}"
        )
    ))
@NoArgsConstructor(access = PRIVATE)
public class OpenApiConfig {

  public static final String SECURITY_SCHEMES_KEYCLOAK = "keycloak";
}

