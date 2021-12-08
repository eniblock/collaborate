package collaborate.api.datasource.passport.metric;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.gateway.GatewayResourceDTO;
import collaborate.api.datasource.gateway.GatewayUrlService;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.passport.find.FindPassportService;
import collaborate.api.datasource.passport.model.DatasourceDTO;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.datasource.passport.model.Metric;
import collaborate.api.user.connected.ConnectedUserService;
import com.alibaba.fastjson.JSONPath;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@RequiredArgsConstructor
@Slf4j
@Service
public class MetricService {

  public static final String VALUE_JSON_PATH = "value.jsonPath";
  public static final String SCOPE_METRIC_PREFIX = "scope:metric:";
  private final Clock clock;
  private final ConnectedUserService connectedUserService;
  private final DatasourceService datasourceService;
  private final FindPassportService findPassportService;
  private final ObjectMapper objectMapper;
  private final GatewayUrlService gatewayUrlService;

  public Optional<Page<Metric>> findAll(Integer tokenId, Pageable pageable, String query) {
    // TODO handle query parameter
    if (StringUtils.isNotBlank(query)) {
      throw new NotImplementedException();
    }
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

  void ensureConnectedUserAllowed(DigitalPassportDetailsDTO passportDetailsDTO) {
    var currentUserWallet = connectedUserService.getWalletAddress();
    var isAllowed =
        currentUserWallet.equals(passportDetailsDTO.getAssetOwner().getAddress())
            || currentUserWallet.equals(passportDetailsDTO.getOperator().getAddress());
    if (!isAllowed) {
      throw new ResponseStatusException(FORBIDDEN);
    }
  }

  Stream<GatewayResourceDTO> buildMetricUrls(
      DigitalPassportDetailsDTO passportDetailsDTO) {
    return passportDetailsDTO
        .getAssetDataCatalog()
        .getDatasources().stream()
        .flatMap(this::buildMetricUrls);
  }

  Stream<GatewayResourceDTO> buildMetricUrls(DatasourceDTO datasourceDTO) {
    Set<Metadata> metadata = datasourceService.getMetadata(datasourceDTO.getId());
    return datasourceDTO.getScopes().stream()
        .filter(s -> startsWith(s, SCOPE_METRIC_PREFIX))
        .map(scope -> GatewayResourceDTO.builder()
            .datasourceId(datasourceDTO.getId())
            .assetIdForDatasource(datasourceDTO.getAssetIdForDatasource())
            .scope(scope)
            .metadata(getScopeMetadata(scope, metadata))
            .build()
        );
  }

  Set<Metadata> getScopeMetadata(String scope, Set<Metadata> metadata) {
    return metadata.stream()
        .filter(m -> startsWith(m.getName(), scope))
        .map(m -> m.toBuilder().name(removeStart(m.getName(), scope).substring(1)).build())
        .collect(toSet());
  }

  List<Metric> fetchMetrics(Stream<GatewayResourceDTO> metricsAndUriResult) {
    return metricsAndUriResult
        .map(
            gtwResource -> CompletableFuture.supplyAsync(() -> {
                  String jsonResponse = requireNonNull(
                      gatewayUrlService
                          .fetch(gtwResource)
                          .getBody()
                  ).toString();
                  return Metric.builder()
                      .updatedAt(ZonedDateTime.now(clock))
                      .scope(gtwResource.getScope())
                      .value(extractValuePath(jsonResponse, gtwResource.getMetadata()))
                      .build();
                }
            )
        ).map(CompletableFuture::join)
        .collect(toList());
  }

  JsonNode extractValuePath(String jsonResponse, Set<Metadata> metadata) {
    Optional<String> jsonPathOpt = Optional.empty();
    var result = jsonResponse;
    if (metadata != null) {
      jsonPathOpt = metadata.stream()
          .filter(m -> VALUE_JSON_PATH.equalsIgnoreCase(m.getName()))
          .map(Metadata::getValue)
          .findFirst();
    }
    if (jsonPathOpt.isPresent()) {
      var jsonPath = JSONPath.compile(jsonPathOpt.get());
      result = "";
      if (jsonPath.contains(jsonResponse)) {
        result = jsonPath.eval(jsonResponse).toString();
      }
    }
    return objectMapper.valueToTree(result);
  }
}
