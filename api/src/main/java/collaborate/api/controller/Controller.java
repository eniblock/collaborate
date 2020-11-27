package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
public class Controller {


    @GetMapping("/users")
    @io.swagger.v3.oas.annotations.Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    public List<String> getAllUsers() {
        List<String> result = new ArrayList<>();

        result.add("Robert");
        result.add("Alice");
        result.add("John");

        return result;
    }

}
