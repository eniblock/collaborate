package collaborate.catalog.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
public class Data implements Serializable {
    @Id
    String id;
    String organizationName;
    Long datasourceId;
    String dataId;
    String title;
    String scope;
    String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", datasourceId=" + datasourceId +
                ", dataId='" + dataId + '\'' +
                ", title='" + title + '\'' +
                ", scope='" + scope + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
