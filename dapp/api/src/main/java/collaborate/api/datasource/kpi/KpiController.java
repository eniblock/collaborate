package collaborate.api.datasource.kpi;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.kpi.find.FindKpiService;
import collaborate.api.datasource.kpi.find.KpiAggregation;
import collaborate.api.datasource.kpi.find.KpiQuery;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/kpi")
@RequiredArgsConstructor
public class KpiController {

  private final FindKpiService findKpiService;

  @GetMapping
  @Operation(description = "Compute an aggregation on the defined KpiQuery", security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DATASOURCE_READ)
  public Collection<KpiAggregation> find(KpiQuery kpiQuery) {
    return findKpiService.find(kpiQuery);
  }

}
