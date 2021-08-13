package collaborate.api.user.security;

public final class Authorizations {

  private Authorizations() {
  }

  public static final class Roles {

    private Roles() {
    }

    public static final String SERVICE_IDP_ADMIN = "service_identity_provider_administrator";
    public static final String PENDING_ASSET_OWNER = "pending_asset_owner";
    public static final String ASSET_OWNER = "asset_owner";
  }

  public static final class HasRoles {

    private HasRoles() {
    }

    public static final String SERVICE_IDP_ADMIN = "hasRole('" + Roles.SERVICE_IDP_ADMIN + "')";
    public static final String PENDING_ASSET_OWNER = "hasRole('" + Roles.PENDING_ASSET_OWNER + "')";
  }

}
