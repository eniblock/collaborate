package collaborate.api.datasource.kpi;

import collaborate.api.datasource.kpi.model.Kpi;
import collaborate.api.datasource.kpi.model.KpiAggregation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface KpiDAO extends JpaRepository<Kpi, Long> {

  @Query(value = ""
      + "WITH groupKeys AS ("
      + " SELECT d.created_at, o.organization_wallet"
      + " FROM (SELECT DISTINCT to_char(created_at, ?2) AS created_at FROM kpi WHERE kpi_key = ?1) d"
      + " CROSS JOIN (SELECT DISTINCT organization_wallet FROM kpi WHERE kpi_key = ?1) o"
      + ") "
      + "SELECT "
      + " g.organization_wallet AS organizationWallet, "
      + " g.created_at AS label,"
      + " COUNT(k.*) AS total "
      + "FROM groupKeys AS g "
      + "LEFT OUTER JOIN kpi AS k ON "
      + " g.created_at = to_char(k.created_at, ?2)"
      + " AND g.organization_wallet = k.organization_wallet "
      + " AND k.kpi_key =?1 "
      + "GROUP BY g.created_at, g.organization_wallet "
      + "ORDER BY g.created_at ASC, g.organization_wallet ASC", nativeQuery = true)
  List<KpiAggregation> countByKeyAndDatetime(String key, String datetimeFormat);

  @Query(value = "SELECT "
      + " k.organization_wallet AS organizationWallet, "
      + " COUNT(k.*) AS total "
      + "FROM kpi AS k "
      + "WHERE k.kpi_key =?1 "
      + "GROUP BY k.organization_wallet ", nativeQuery = true)
  List<KpiAggregation> countByOrganization(String key);

}
