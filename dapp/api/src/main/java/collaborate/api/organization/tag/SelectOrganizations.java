package collaborate.api.organization.tag;

import lombok.Getter;

@Getter
public class SelectOrganizations {

  private final String[] dataFields = {"organizations"};

  private SelectOrganizations() {
  }
  private static final SelectOrganizations INSTANCE = new SelectOrganizations();

  public static SelectOrganizations getInstance() {
    return INSTANCE;
  }
}
