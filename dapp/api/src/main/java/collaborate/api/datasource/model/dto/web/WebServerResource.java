package collaborate.api.datasource.model.dto.web;

import static lombok.AccessLevel.PRIVATE;

import collaborate.api.datasource.model.dto.Keywords;
import collaborate.api.datasource.model.dto.NoQueryStringConstraint;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class WebServerResource implements Keywords<String>, Serializable {

  @NoArgsConstructor(access = PRIVATE)
  public static class Keywords {

    public static final String SCOPE_ASSET_LIST = "scope:list-asset";
  }

  @Schema(description = "A human readable description of this resource")
  private String description;

  @NotEmpty
  @ResourceKeywordConstraint
  @ArraySchema(schema = @Schema(description =
      "Keyword can be used to extend feature about a resource."
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
  private ArrayList<QueryParam> queryParams;

}
