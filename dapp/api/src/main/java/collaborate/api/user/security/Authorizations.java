package collaborate.api.user.security;

public final class Authorizations {

  private Authorizations() {
  }

  public static final class Roles {

    private Roles() {
    }

    public static final String SERVICE_IDP_ADMIN = "service_identity_provider_administrator";
    public static final String SERVICE_PROVIDER_ADMIN = "service_provider_administrator";
    public static final String SERVICE_PROVIDER_OPERATOR = "service_provider_operator";
    public static final String PENDING_ASSET_OWNER = "pending_asset_owner";
    public static final String ASSET_OWNER = "asset_owner";
  }

  public static final class HasRoles {

    private HasRoles() {
    }

    /*
     * Atomic roles
     */
    public static final String SERVICE_IDP_ADMIN = "hasRole('" + Roles.SERVICE_IDP_ADMIN + "')";
    public static final String SERVICE_PROVIDER_ADMIN =
        "hasRole('" + Roles.SERVICE_PROVIDER_ADMIN + "')";
    public static final String SERVICE_PROVIDER_OPERATOR =
        "hasRole('" + Roles.SERVICE_PROVIDER_OPERATOR + "')";
    public static final String PENDING_ASSET_OWNER = "hasRole('" + Roles.PENDING_ASSET_OWNER + "')";
    public static final String ASSET_OWNER = "hasRole('" + Roles.ASSET_OWNER + "')";

    /*
     * Aggregated roles
     */
    public static final String SERVICE_PROVIDER =
        HasRoles.SERVICE_PROVIDER_OPERATOR + " OR " + HasRoles.SERVICE_PROVIDER_ADMIN;
    public static final String ASSET_OWNER_OR_SERVICE_PROVIDER =
        HasRoles.ASSET_OWNER + " OR " + HasRoles.SERVICE_PROVIDER;
  }

}
