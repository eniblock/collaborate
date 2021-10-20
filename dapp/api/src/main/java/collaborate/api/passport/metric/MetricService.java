package collaborate.api.passport.metric;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.model.Attribute;
import collaborate.api.gateway.GatewayUrlService;
import collaborate.api.passport.find.FindPassportService;
import collaborate.api.passport.model.DatasourceDTO;
import collaborate.api.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.passport.model.Metric;
import collaborate.api.user.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
@Slf4j
@Service
public class MetricService {

  public static final String VALUE_JSON_PATH = "value.jsonPath";
  public static final String SCOPE_METRIC_PREFIX = "scope:metric:";
  private final Clock clock;
  private final DatasourceService datasourceService;
  private final FindPassportService findPassportService;
  private final ObjectMapper objectMapper;
  private final GatewayUrlService gatewayUrlService;
  private final TraefikProperties traefikProperties;
  private final UserService userService;

  public Optional<Page<Metric>> findAll(Integer tokenId, Pageable pageable, String query) {
    // TODO handle query parameter
    log.warn("query={} is not implemented", query);
    var passportDtoOpt = findPassportService.findPassportDetailsByTokenId(tokenId);
    if (passportDtoOpt.isEmpty()) {
      log.info("No passport found for tokenId={}", tokenId);
      return Optional.empty();
    } else {
      var passportDetailsDTO = passportDtoOpt.get();
      ensureConnectedUserAllowed(passportDetailsDTO);
      var pagedMetricsAndUri =
          buildMetricUrls(passportDetailsDTO)
              .skip(pageable.getOffset())
              .limit(pageable.getPageSize());

      return Optional.of(
          new PageImpl<>(
              fetchMetrics(pagedMetricsAndUri),
              pageable,
              passportDetailsDTO.countScopes()
          )
      );
    }
  }

  void ensureConnectedUserAllowed(
      DigitalPassportDetailsDTO passportDetailsDTO) {
    var currentUserWallet = userService.getConnectedUserWallet().getAddress();
    var isAllowed =
        currentUserWallet.equals(passportDetailsDTO.getAssetOwner().getAddress())
            || currentUserWallet.equals(passportDetailsDTO.getOperator().getAddress());
    if (!isAllowed) {
      throw new ResponseStatusException(FORBIDDEN);
    }
  }

  Stream<MetricGatewayDTO> buildMetricUrls(
      DigitalPassportDetailsDTO passportDetailsDTO) {
    return passportDetailsDTO
        .getAssetDataCatalog()
        .getDatasources().stream()
        .flatMap(this::buildMetricUrls);
  }

  Stream<MetricGatewayDTO> buildMetricUrls(DatasourceDTO datasourceDTO) {
    Set<Attribute> metadata = datasourceService.getMetadata(datasourceDTO.getId());
    var urlPrefix = UriComponentsBuilder.fromUriString(traefikProperties.getUrl())
        .path("/datasource")
        .path("/" + datasourceDTO.getId()).build().toUriString();
    return datasourceDTO.getScopes().stream()
        .filter(s -> startsWith(s, SCOPE_METRIC_PREFIX))
        .map(scope -> MetricGatewayDTO.builder()
            .scope(removeStart(scope, SCOPE_METRIC_PREFIX))
            .uri(UriComponentsBuilder
                .fromUriString(urlPrefix)
                .path("/" + scope)
                .path("/" + datasourceDTO.getAssetIdForDatasource())
                .toUriString()
            ).metadata(getScopeMetadata(scope, metadata))
            .build()
        );
  }

  Set<Attribute> getScopeMetadata(String scope, Set<Attribute> metadata) {
    return metadata.stream()
        .filter(m -> startsWith(m.getName(), scope))
        .map(m -> m.toBuilder().name(removeStart(m.getName(), scope).substring(1)).build())
        .collect(toSet());
  }

  List<Metric> fetchMetrics(Stream<MetricGatewayDTO> metricsAndUriResult) {
    return metricsAndUriResult
        .map(
            metricDTO -> CompletableFuture.supplyAsync(() -> {
                  String jsonResponse = gatewayUrlService.fetch(metricDTO.getUri()).toString();
                  return Metric.builder()
                      .updatedAt(ZonedDateTime.now(clock))
                      .scope(metricDTO.getScope())
                      .value(extractValuePath(jsonResponse, metricDTO.getMetadata()))
                      .build();
                }
            )
        ).map(CompletableFuture::join)
        .collect(toList());
  }

  JsonNode extractValuePath(String jsonResponse, Set<Attribute> metadata) {
    Optional<String> jsonPath = Optional.empty();
    if (metadata != null) {
      jsonPath = metadata.stream()
          .filter(m -> VALUE_JSON_PATH.equalsIgnoreCase(m.getName()))
          .map(Attribute::getValue)
          .findFirst();
    }

    return jsonPath.map(p -> JsonPath.read(jsonResponse, p))
        .map(o -> objectMapper.<JsonNode>valueToTree(o.toString()))
        .orElse(objectMapper.valueToTree(jsonResponse));
  }
}
