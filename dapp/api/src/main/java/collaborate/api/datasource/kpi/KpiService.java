package collaborate.api.datasource.kpi;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import collaborate.api.datasource.kpi.model.KpiAggregation;
import collaborate.api.datasource.kpi.model.KpiDataSet;
import collaborate.api.datasource.kpi.model.KpiQuery;
import collaborate.api.datasource.kpi.model.KpiResult;
import collaborate.api.organization.tag.Organization;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KpiService {

  private final KpiDAO kpiDAO;

  public KpiResult find(KpiQuery query) {
    var kpiAggregations = kpiDAO.countByKeyAndDatetime(
        query.getKpiKey(),
        query.getDatetimeFormat()
    );
    return buildKpiResult(query.getKpiKey(), kpiAggregations);
  }

  public KpiResult buildKpiResult(String kpiKey, List<KpiAggregation> aggregations) {
    LinkedHashSet<String> labels = buildLabels(aggregations);

    List<KpiDataSet> dataSets = buildDataSets(
        aggregations);

    return new KpiResult(kpiKey, labels, dataSets);
  }

  private LinkedHashSet<String> buildLabels(List<KpiAggregation> aggregations) {
    return aggregations
        .stream()
        .map(KpiAggregation::getLabel)
        .collect(toCollection(LinkedHashSet::new));
  }

  private List<KpiDataSet> buildDataSets(List<KpiAggregation> aggregations) {
    var byWallet = aggregations
        .stream()
        .collect(groupingBy(KpiAggregation::getOrganizationWallet));

    return byWallet.entrySet().stream().map(e ->
        new KpiDataSet(
            Organization.builder()
                .address(e.getKey())
                .build(),
            e.getValue()
                .stream()
                .map(c -> Optional.ofNullable(c.getTotal()).orElse(0L))
                .collect(toList())
        )
    ).collect(toList());
  }


}
