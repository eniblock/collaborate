package collaborate.api.organization.tag;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-organization-client")
public interface TezosApiGatewayOrganizationClient {

  /**
   * @param contractAddress the smartContract address
   * @param selectQuery The select field query
   * @return The list of organizations stored in the smartContract
   */
  @PostMapping("tezos_node/storage/{contractAddress}")
  OrganizationsResponse getOrganizations(@PathVariable String contractAddress,
      @RequestBody SelectOrganizations selectQuery);
}
