package collaborate.catalog.domain;

import java.io.Serializable;

public class Data implements Serializable {
    Long datasourceId;
    String dataId;
    String title;
    String scope;
    String type;

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
                "datasourceId=" + datasourceId +
                ", dataId='" + dataId + '\'' +
                ", title='" + title + '\'' +
                ", scope='" + scope + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
