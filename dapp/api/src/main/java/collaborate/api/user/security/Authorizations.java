package collaborate.api.user.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Authorizations {

  private Authorizations() {
  }

  public static final class Roles {

    private Roles() {
    }

    /**
     * As Eric a service provider identity administrator
     */
    public static final String IDENTITY_ADMIN = "service_identity_provider_administrator";

    /**
     * As Sam a data service provider administrator
     */
    public static final String DSP_ADMIN = "data_service_provider_administrator";

    public static final String BSP_ADMIN = "business_service_provider_administrator";

    public static final String DSP_OPERATOR = "data_service_provider_operator";

    /**
     * As David a business service provider operator
     */
    public static final String BSP_OPERATOR = "business_service_provider_operator";

    /**
     * As Alice, a vehicle owner
     */
    public static final String PENDING_ASSET_OWNER = "pending_asset_owner";

    /**
     * As Alice, a vehicle owner
     */
    public static final String ASSET_OWNER = "asset_owner";

    public static final Collection<String> BSP_ROLES = Set.of(
        BSP_ADMIN, BSP_OPERATOR);
    public static final Collection<String> DSP_ROLES = Set.of(
        DSP_ADMIN, DSP_OPERATOR);

    public static final Collection<String> ORGANIZATION_ROLES =
        Stream.of(BSP_ROLES, DSP_ROLES, List.of(IDENTITY_ADMIN))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

  }

  public static final class HasRoles {

    private HasRoles() {
    }

    /*
     * Atomic roles
     */
    public static final String IDENTITY_ADMIN = "hasRole('" + Roles.IDENTITY_ADMIN + "')";
    public static final String DSP_ADMIN = "hasRole('" + Roles.DSP_ADMIN + "')";
    public static final String BSP_ADMIN = "hasRole('" + Roles.BSP_ADMIN + "')";
    public static final String DSP_OPERATOR = "hasRole('" + Roles.DSP_OPERATOR + "')";
    public static final String BSP_OPERATOR = "hasRole('" + Roles.BSP_OPERATOR + "')";
    public static final String PENDING_ASSET_OWNER = "hasRole('" + Roles.PENDING_ASSET_OWNER + "')";
    public static final String ASSET_OWNER = "hasRole('" + Roles.ASSET_OWNER + "')";

    /*
     * Aggregated roles
     */
    public static final String BSP = HasRoles.BSP_OPERATOR + " OR " + HasRoles.BSP_ADMIN;
    public static final String DSP = HasRoles.DSP_OPERATOR + " OR " + HasRoles.DSP_ADMIN;
    public static final String ORGANIZATION_READ = HasRoles.BSP + " OR " + HasRoles.DSP;
    public static final String DATASOURCE_READ =
        HasRoles.DSP_ADMIN
            + " OR " + HasRoles.BSP_OPERATOR;
    public static final String DIGITAL_PASSPORT_READ =
        HasRoles.ASSET_OWNER +
            " OR " + HasRoles.BSP +
            " OR " + HasRoles.DSP;
    public static final String API_GATEWAY_READ =
        HasRoles.DSP_ADMIN +
            " OR " + HasRoles.ASSET_OWNER;
    public static final String PASSPORT_MULTISIG_READ =
        HasRoles.DSP_ADMIN
            + " OR " + HasRoles.ASSET_OWNER
            + " OR " + HasRoles.BSP_ADMIN;
  }

}
