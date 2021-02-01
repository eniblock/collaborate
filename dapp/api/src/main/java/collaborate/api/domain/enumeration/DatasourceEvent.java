package collaborate.api.domain.enumeration;

public enum DatasourceEvent {
    CREATED("datasource.created"),
    SYNCHRONIZE("datasource.synchronize");

    private String event;

    DatasourceEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
