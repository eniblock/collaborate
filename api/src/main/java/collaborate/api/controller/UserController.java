package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.services.UserService;
import collaborate.api.services.dto.UserDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService service) {
        this.userService = service;
    }

    @GetMapping("/users")
    @io.swagger.v3.oas.annotations.Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    public Page<UserDTO> listUsersByPage(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size,
        String sort
    ) {
        Sort sortOption;

        if(sort != null && (sort.equalsIgnoreCase("asc") || sort.equalsIgnoreCase("desc"))) {
            try {
                sortOption = Sort.by(Sort.Direction.fromString(sort));
            } catch (IllegalArgumentException e) {
                log.error("Unexpected error happen while create sort option");
                sortOption = Sort.unsorted();
            }
        } else {
            sortOption = Sort.unsorted();
        }

        Pageable pageable = PageRequest.of(page, size, sortOption);
        return userService.listUsers(pageable);
    }

}
