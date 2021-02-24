package collaborate.api.services.connectors.fakedatasource;

public class Links {
    private HrefLink self;
    private HrefLink download;

    public HrefLink getSelf() {
        return self;
    }

    public void setSelf(HrefLink self) {
        this.self = self;
    }

    public HrefLink getDownload() {
        return download;
    }

    public void setDownload(HrefLink download) {
        this.download = download;
    }
}