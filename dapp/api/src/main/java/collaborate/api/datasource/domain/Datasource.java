package collaborate.api.datasource.domain;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import collaborate.api.config.StringSetConverter;
import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.authentication.Authentication;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, visible = true, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = WebServerDatasource.class, name = "WebServerDatasource"),
})
@Data
@NoArgsConstructor
@Entity
@Table(name = "Datasource")
@JsonInclude(Include.NON_NULL)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Datasource implements Keywords<String>, Serializable {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Type(type = "pg-uuid")
  protected UUID id;

  @Schema(description = "A simple name describiung this datasource", example = "PSA digital-passports")
  protected String name;

  @Schema(description = "Deprecated", example = "CREATED")
  @Enumerated(EnumType.STRING)
  protected DatasourceStatus status = DatasourceStatus.CREATED;

  /**
   * DB inheritance field
   */
  @Schema(description = "Used for serialization", example = "WebServerDatasource")
  private String type;

  @Schema(description = "The authentication method to use for consuming this datasource<br>")
  @OneToOne(cascade = CascadeType.ALL)
  protected Authentication authMethod;

  @ArraySchema(schema = @Schema(
      description = "Used to make datasource extensible. "
          + "For an example it could be used to filter a datasource list by content",
      example = "contains:digital-passports"
  ), uniqueItems = true)
  @Convert(converter = StringSetConverter.class)
  protected Set<String> keywords;

  protected Datasource(UUID id, String name, DatasourceStatus status, Authentication authMethod,
      Set<String> keywords) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.authMethod = authMethod;
    this.keywords = keywords;
  }

  public abstract void accept(DatasourceVisitor visitor);

  public boolean anyKeywordsContains(String searched) {
    return keywords != null && getKeywords().stream()
        .anyMatch(s -> s.contains(searched));
  }

}
