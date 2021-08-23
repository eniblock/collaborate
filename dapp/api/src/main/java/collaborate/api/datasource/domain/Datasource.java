package collaborate.api.datasource.domain;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import collaborate.api.datasource.config.StringListConverter;
import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.authentication.Authentication;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.List;
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

  protected String name;

  @Enumerated(EnumType.STRING)
  protected DatasourceStatus status = DatasourceStatus.CREATED;

  /**
   * DB inheritance field
   */
  private String type;

  @OneToOne(cascade = CascadeType.ALL)
  protected Authentication authMethod;

  @Convert(converter = StringListConverter.class)
  protected List<String> keywords;

  protected Datasource(UUID id, String name, DatasourceStatus status, Authentication authMethod,
      List<String> keywords) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.authMethod = authMethod;
    this.keywords = keywords;
  }

  public abstract void accept(DatasourceVisitor visitor);

  public boolean anyKeywordsContains(String searched) {
    return keywords != null && getKeywords().stream()
        .anyMatch(s -> s.equals(searched));
  }

}
