package collaborate.api.traefik;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.config.api.YamlMapper;
import collaborate.api.traefik.domain.EntryPoint;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/traefik")
@RequiredArgsConstructor
public class TraefikController {

  private final ApiProperties apiProperties;
  private final YamlMapper yamlMapper;

  @GetMapping
  @Operation(
      description = "Get the list of the Traefik providers",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(HasRoles.SERVICE_PROVIDER_ADMIN)
  public List<EntryPoint> list() throws IOException {
    Collection<File> files;
    try (Stream<Path> stream = Files.list(apiProperties.getTraefik().getProvidersPath())) {
      files = stream
          .filter(file -> !Files.isDirectory(file))
          .map(Path::toFile)
          .collect(Collectors.toSet());
    }
    List<EntryPoint> providers = new ArrayList<>();
    for (File file : files) {
      providers.add(yamlMapper.readValue(file, EntryPoint.class));
    }
    return providers;
  }
}
