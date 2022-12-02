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

    public static final String BNO = "business_network_operator";

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

    private static final Collection<String> ORGANIZATION_ROLES =
        Stream.of(BSP_ROLES, DSP_ROLES, List.of(IDENTITY_ADMIN))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    public static Collection<String> getOrganizationRoles() {
      return ORGANIZATION_ROLES;
    }
  }

  public static final class HasRoles {

    private HasRoles() {
    }

    private static final String HAS_ROLE = "hasRole";
    /*
     * Atomic roles
     */
    public static final String IDENTITY_ADMIN = HAS_ROLE + "('" + Roles.IDENTITY_ADMIN + "')";

    public static final String BNO = HAS_ROLE + "('" + Roles.BNO + "')";

    public static final String BSP_ADMIN = HAS_ROLE + "('" + Roles.BSP_ADMIN + "')";
    public static final String DSP_ADMIN = HAS_ROLE + "('" + Roles.DSP_ADMIN + "')";
    public static final String DSP_OPERATOR = HAS_ROLE + "('" + Roles.DSP_OPERATOR + "')";
    public static final String BSP_OPERATOR = HAS_ROLE + "('" + Roles.BSP_OPERATOR + "')";

    public static final String PENDING_ASSET_OWNER =
        HAS_ROLE + "('" + Roles.PENDING_ASSET_OWNER + "')";
    public static final String ASSET_OWNER = HAS_ROLE + "('" + Roles.ASSET_OWNER + "')";

    /*
     * Aggregated roles
     */
    public static final String BSP =
        HasRoles.BSP_OPERATOR
            + " OR " + HasRoles.BSP_ADMIN
            + " OR " + HasRoles.IDENTITY_ADMIN;
    public static final String DSP =
        HasRoles.DSP_OPERATOR
            + " OR " + HasRoles.DSP_ADMIN
            + " OR " + HasRoles.IDENTITY_ADMIN;
    public static final String ORGANIZATION_READ =
        HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO;

    public static final String BUSINESS_DATA_READ =
        HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO;

    public static final String BUSINESS_DATA_GRANT_ACCESS_REQUEST =
        HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO
        ;

    public static final String KPI_READ =
        HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO
        ;

    public static final String DATASOURCE_READ =
        HasRoles.DSP_ADMIN
            + " OR " + HasRoles.BSP_OPERATOR
            + " OR " + HasRoles.BNO
        ;

    public static final String DIGITAL_PASSPORT_READ =
        HasRoles.ASSET_OWNER
            + " OR " + HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO
        ;

    public static final String API_GATEWAY_READ =
        HasRoles.DSP_ADMIN
            + " OR " + HasRoles.ASSET_OWNER
            + " OR " + HasRoles.BNO
        ;

    public static final String PASSPORT_MULTISIG_READ =
        HasRoles.DSP_ADMIN
            + " OR " + HasRoles.ASSET_OWNER
            + " OR " + HasRoles.BSP_ADMIN
            + " OR " + HasRoles.BNO
        ;

    public static final String WALLET_READ =
        IDENTITY_ADMIN
            + "OR " + DSP_ADMIN
            + "OR " + BSP_ADMIN
            + "OR " + DSP_OPERATOR
            + "OR " + ASSET_OWNER
            + " OR " + BNO
        ;

    public static final String USER_READ =
        IDENTITY_ADMIN
            + "OR " + DSP_ADMIN
            + "OR " + BSP_ADMIN
            + "OR " + DSP_OPERATOR
            + "OR " + BSP_OPERATOR
            + " OR " + BNO
        ;

    public static final String SERVICE_DATA_WRITE =
        HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO
            + " OR " + HasRoles.BSP_ADMIN
            + "OR " + BSP_OPERATOR
            + "OR " + DSP_ADMIN
        ;

    public static final String SERVICE_DATA_READ =
        HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO
            + " OR " + HasRoles.BSP_ADMIN
            + "OR " + BSP_OPERATOR
            + "OR " + DSP_ADMIN
        ;

    public static final String SERVICE_DATA_GRANT_ACCESS_REQUEST =
        HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO
            + " OR " + HasRoles.BSP_ADMIN
            + "OR " + BSP_OPERATOR
            + "OR " + DSP_ADMIN
        ;

    public static final String SERVICE_CONSENT_READ =
        HasRoles.ASSET_OWNER
            + " OR " + HasRoles.BSP
            + " OR " + HasRoles.DSP
            + " OR " + HasRoles.BNO
            + " OR " + HasRoles.BSP_ADMIN
            + "OR " + BSP_OPERATOR
            + "OR " + DSP_ADMIN
        ;

    public static final String SERVICE_CONSENT_MULTISIG_READ =
        HasRoles.DSP_ADMIN
            + " OR " + HasRoles.ASSET_OWNER
            + " OR " + HasRoles.BNO
            + " OR " + HasRoles.BSP_ADMIN
            + "OR " + BSP_OPERATOR
            + "OR " + DSP_ADMIN
        ;

  }

}
