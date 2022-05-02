package collaborate.api.datasource.model.dto.web;

import static lombok.AccessLevel.PRIVATE;

import collaborate.api.datasource.model.dto.Keywords;
import collaborate.api.datasource.model.dto.NoQueryStringConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
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
public class WebServerResource implements Keywords<Attribute>, Serializable {

  @NoArgsConstructor(access = PRIVATE)
  public static class Keywords {

    public static final String ATTR_NAME_TEST_CONNECTION = "list-asset";
    public static final String ATTR_NAME_SCOPE = "scope";
  }

  @Schema(description = "A human readable description of this resource")
  private String description;

  @NotEmpty
  @ResourceKeywordConstraint
  @ArraySchema(schema = @Schema(description =
      "Keyword can be used to extend feature about a resource."
          + "For an example, integrated to an API Gateway, can be used to defined the routing path for this resource",
      example = "routing-key:assets"))
  private Set<Attribute> keywords;

  @NoQueryStringConstraint
  @Schema(description = "The resource path relative to the datasource baseUrl<br>"
      + "<b>NB</b>When a resource path contains path parameter, this parameter should be included using $i format",
      example = "/connected-vehicles/$1/odometer")
  private String url;

  @OneToMany(cascade = CascadeType.ALL)
  @ArraySchema(schema = @Schema(description = "Query parameter to add at the end of this resource URL. "))
  private ArrayList<QueryParam> queryParams;

  @JsonIgnore
  public Optional<String> findFirstKeywordValueByName(String name) {
    return Attribute.findFirstByName(keywords, name)
        .map(Attribute::getValue);
  }

  @JsonIgnore
  public boolean keywordsContainsName(String name) {
    return Attribute.containsName(keywords, name);
  }
}
