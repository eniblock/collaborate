package collaborate.api.passport;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.passport.create.CreatePassportDTO;
import collaborate.api.tag.model.Job;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/digital-passport")
@Tag(name = "digital-passport", description = "the Digital-passport API")
@RequiredArgsConstructor
public class DigitalPassportController {

  private final PassportService passportService;

  @PostMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Create a multi-signature entry in the Smart-Contract.<br>"
          + "A multi-signature is used as a \"pending\" digital-passport<br>"
          + "The DSP signature is automatically added",
      tags = {"multisig", "multi-signature"}
  )
  @PreAuthorize(HasRoles.SERVICE_PROVIDER)
  public Job create(@RequestBody @Valid CreatePassportDTO createPassportDTO) {
    return passportService.create(createPassportDTO);
  }

  @GetMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(HasRoles.SERVICE_PROVIDER)
  public HttpEntity<List<DigitalPassportDTO>> list() {

    List<DigitalPassportDTO> listOfPassorts = new ArrayList<>();
    DigitalPassportDTO obj1 = new DigitalPassportDTO();

    obj1.setDspAddress("psa");
    obj1.setDspName("psa groupe");
    obj1.setDatasourceUUID(UUID.randomUUID());
    obj1.setVin("1FTFX1E57JKE37091");

    DigitalPassportDTO obj2 = new DigitalPassportDTO();
    obj2.setDspAddress("psa");
    obj2.setDspName("psa groupe");
    obj2.setDatasourceUUID(UUID.randomUUID());
    obj2.setVin("1FTFX1E57JKE37092");

    DigitalPassportDTO obj3 = new DigitalPassportDTO();
    obj3.setDspAddress("psa");
    obj3.setDspName("psa groupe");
    obj3.setDatasourceUUID(UUID.randomUUID());
    obj3.setVin("1FTFX1E57JKE37094");

    DigitalPassportDTO obj4 = new DigitalPassportDTO();
    obj4.setDspAddress("psa");
    obj4.setDspName("psa groupe");
    obj4.setDatasourceUUID(UUID.randomUUID());
    obj4.setVin("1FTFX1E57JKE37095");

    listOfPassorts.add(obj1);
    listOfPassorts.add(obj2);
    listOfPassorts.add(obj3);
    listOfPassorts.add(obj4);

    return ResponseEntity.ok(listOfPassorts);
  }


}
