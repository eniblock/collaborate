package collaborate.api.organization;

import static collaborate.api.tag.TezosApiGatewayJobClient.ORGANIZATION_SECURE_KEY_NAME;
import static java.util.stream.Collectors.toList;

import collaborate.api.config.api.SmartContractAddressProperties;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.organization.tag.TezosApiGatewayOrganizationClient;
import collaborate.api.organization.tag.UpdateOrganisationFactory;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.job.Transaction;
import collaborate.api.tag.model.job.TransactionBatch;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrganizationDAO {

  private static final String UPDATE_ORGANIZATIONS_ENTRYPOINT = "update_organizations";
  private final ModelMapper modelMapper;
  private final SmartContractAddressProperties smartContractAddressProperties;
  private final TezosApiGatewayOrganizationClient tezosApiGatewayOrganizationClient;
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final UpdateOrganisationFactory updateOrganisationFactory;

  public static final String ORGANIZATION_FIELD = "organizations";
  static final DataFieldsRequest<String> GET_ALL_ORGANIZATIONS_REQUEST = new DataFieldsRequest<>(
      List.of(ORGANIZATION_FIELD));

  public Collection<OrganizationDTO> getAllOrganizations() {
    var organizations = tezosApiGatewayOrganizationClient.getOrganizations(
        smartContractAddressProperties.getOrganizationYellowPage(),
        GET_ALL_ORGANIZATIONS_REQUEST
    );

    if (organizations.get(ORGANIZATION_FIELD) == null) {
      return Collections.emptyList();
    } else {
      return Arrays.stream(organizations.get(ORGANIZATION_FIELD))
          .map(TagEntry::getValue)
          .map(organization -> modelMapper.map(organization, OrganizationDTO.class))
          .collect(toList());
    }
  }

  public Optional<OrganizationDTO> findOrganizationByPublicKeyHash(String address) {
    return getAllOrganizations().stream()
        .filter(o -> address.equals(o.getAddress()))
        .findFirst();
  }

  public void upsert(OrganizationDTO organization) {
    var updateOrgType = List.of(updateOrganisationFactory.update(organization));
    var updateYellowPage = Transaction.builder()
        .contractAddress(smartContractAddressProperties.getOrganizationYellowPage())
        .entryPoint(UPDATE_ORGANIZATIONS_ENTRYPOINT)
        .entryPointParams(updateOrgType)
        .build();
    var updateBusinessData = Transaction.builder()
        .contractAddress(smartContractAddressProperties.getBusinessData())
        .entryPoint(UPDATE_ORGANIZATIONS_ENTRYPOINT)
        .entryPointParams(updateOrgType)
        .build();
    var transactionBatch = new TransactionBatch<>(
        List.of(updateYellowPage, updateBusinessData),
        ORGANIZATION_SECURE_KEY_NAME
    );
    tezosApiGatewayJobClient.sendTransactionBatch(transactionBatch, false);
  }
}

