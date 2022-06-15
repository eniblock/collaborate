package collaborate.api.datasource.kpi;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.kpi.find.FindKpiService;
import collaborate.api.datasource.kpi.find.KpiAggregation;
import collaborate.api.datasource.kpi.find.KpiQuery;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/kpi")
@Tag(name = "kpi", description = "The analytics API")
@RequiredArgsConstructor
public class KpiController {

  private final FindKpiService findKpiService;
  private final KpiService kpiService;

  @PostMapping
  @Operation(
      description = "Compute an aggregation on the defined KpiQuery",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.KPI_READ)
  public Collection<KpiAggregation> findKpiByQuery(@Valid @RequestBody KpiQuery kpiQuery) {
    return findKpiService.find(kpiQuery);
  }

  @GetMapping
  @PreAuthorize(HasRoles.KPI_READ)
  public List<Kpi> findKpiByQuery(@RequestParam Integer nftId) {
    return kpiService.find(new KpiSpecification("nft-id", String.valueOf(nftId)));
  }

}
