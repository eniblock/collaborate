package collaborate.api.datasource.domain.web;

import collaborate.api.datasource.config.StringListConverter;
import collaborate.api.datasource.domain.Keywords;
import collaborate.api.datasource.domain.NoQueryStringConstraint;
import collaborate.api.traefik.routing.RoutingKeyKeywordConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@Entity
public class WebServerResource implements Keywords<String>, Serializable {

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="datasource_id", insertable = false, updatable = false)
  private WebServerDatasource webServerDatasource;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String description;

  @Convert(converter = StringListConverter.class)
  @RoutingKeyKeywordConstraint
  private List<String> keywords;

  @NoQueryStringConstraint
  private String url;

  @OneToMany(cascade = CascadeType.ALL)
  private List<QueryParam> queryParams;
}
