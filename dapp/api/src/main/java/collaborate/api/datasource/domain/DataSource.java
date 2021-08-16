package collaborate.api.datasource.domain;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import collaborate.api.datasource.config.StringListConverter;
import collaborate.api.datasource.domain.authentication.Authentication;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.enumeration.DatasourceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.List;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, visible = true, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = WebServerDatasource.class, name = "WEBSERVER"),
})
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "DataSource")
@JsonInclude(Include.NON_NULL)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class DataSource implements Keywords<String>, Serializable {

  @Id
  @GeneratedValue
  protected Long id;

  protected String name;

  @Enumerated(EnumType.STRING)
  protected DatasourceStatus status = DatasourceStatus.CREATED;

  private String type;

  @OneToOne(cascade = CascadeType.ALL)
  protected Authentication authMethod;

  @Convert(converter = StringListConverter.class)
  protected List<String> keywords;

  protected DataSource(Long id, String name,DatasourceStatus status, Authentication authMethod,
      List<String> keywords) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.authMethod = authMethod;
    this.keywords = keywords;
  }
}
