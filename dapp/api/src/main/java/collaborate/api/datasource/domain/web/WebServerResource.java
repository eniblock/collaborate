package collaborate.api.datasource.domain.web;

import collaborate.api.config.StringSetConverter;
import collaborate.api.datasource.domain.Keywords;
import collaborate.api.datasource.domain.NoQueryStringConstraint;
import collaborate.api.datasource.traefik.routing.RoutingKeyKeywordConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
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
  @JoinColumn(name = "datasource_id", insertable = false, updatable = false)
  private WebServerDatasource webServerDatasource;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "The resource identifier relative to the datasource owner")
  private Long id;

  @Schema(description = "A human readable description of this resource")
  private String description;

  @Convert(converter = StringSetConverter.class)
  @RoutingKeyKeywordConstraint
  @ArraySchema(schema = @Schema(description = "Keyword can be used to extend feature about a resource."
      + "For an example, integrated to an API Gateway, can be used to defined the routing path for this resource",
      example = "routing-key:assets"))
  private Set<String> keywords;

  @NoQueryStringConstraint
  @Schema(description = "The resource path relative to the datasource baseUrl<br>"
      + "<b>NB</b>When a resource path contains path parameter, this parameter should be included using $i format",
      example = "/connected-vehicles/$1/odometer")
  private String url;

  @OneToMany(cascade = CascadeType.ALL)
  @ArraySchema(schema = @Schema(description = "Query parameter to add at the end of this resource URL. "))
  private List<QueryParam> queryParams;
}
