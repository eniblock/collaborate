package collaborate.api.datasource.model.dto;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashSet;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, visible = true, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = WebServerDatasourceDTO.class, name = "WebServerDatasource"),
})
@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
public abstract class DatasourceDTO implements Keywords<String>, Serializable {

  protected UUID id;

  @Schema(
      description = "A simple name describing this datasource",
      example = "DSPConsortium1 digital-passports")
  protected String name;

  @Schema(description = "The authentication method to use for consuming this datasource<br>")
  @OneToOne(cascade = CascadeType.ALL)
  protected Authentication authMethod;

  @ArraySchema(
      schema =
      @Schema(
          description =
              "Used to make datasource extensible. "
                  + "For an example it could be used to filter a datasource list by content",
          example = "contains:digital-passports"),
      uniqueItems = true)
  @NotEmpty
  @DatasourcePurposeConstraint
  protected HashSet<String> keywords;

  /**
   * DB inheritance field
   */
  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  private String type;

  protected DatasourceDTO(UUID id, String name, Authentication authMethod,
      HashSet<String> keywords) {
    this.id = id;
    this.name = name;
    this.authMethod = authMethod;
    this.keywords = keywords;
  }

  public abstract <T> T accept(DatasourceDTOVisitor<T> visitor) throws DatasourceVisitorException;

}
