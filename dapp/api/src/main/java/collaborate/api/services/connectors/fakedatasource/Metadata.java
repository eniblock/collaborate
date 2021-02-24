package collaborate.api.services.connectors.fakedatasource;

public class Metadata {
    private String id;
    private String title;
    private String scope;
    private Links _links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Links getLinks() {
        return _links;
    }

    public void setLinks(Links _links) {
        this._links = _links;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
