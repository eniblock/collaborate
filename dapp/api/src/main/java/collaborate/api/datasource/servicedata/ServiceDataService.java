package collaborate.api.datasource.servicedata;

import static java.util.stream.Collectors.toSet;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.servicedata.create.MintServiceDataService;
import collaborate.api.datasource.DatasourceProperties;
import collaborate.api.datasource.model.dto.DatasourceDTOVisitor;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;

import collaborate.api.datasource.servicedata.model.ServiceData;
import collaborate.api.datasource.servicedata.model.ServiceDataDTOVisitor;
import collaborate.api.datasource.servicedata.model.ServiceDataDTOMetadataVisitor;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.servicedata.model.ServiceDataDTO;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsDAO;
import collaborate.api.organization.OrganizationService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ServiceDataService {

  private final ServiceDataRepository serviceDataRepository;
  private final ObjectMapper objectMapper;
  private final OrganizationService organizationService;
  private final MintServiceDataService mintServiceDataService;
  private final UUIDGenerator uuidGenerator;
  private final Clock clock;
  private final DatasourceProperties datasourceProperties;
  private final DateFormatterFactory dateFormatterFactory;
  private final IpfsDAO ipfsDAO;
  private final ServiceDataDTOMetadataVisitor serviceDataDTOMetadataVisitor;

  public ServiceData create(ServiceDataDTO serviceDataDTO) throws IOException {

    serviceDataDTO.setId(uuidGenerator.randomUUID());
    
    var savedServiceData = build(serviceDataDTO);
 
    var datasourcePath = Path.of(datasourceProperties.getRootFolder(),
        dateFormatterFactory.forPattern(datasourceProperties.getPartitionDatePattern()),
        savedServiceData.getId()
    );
    var cid = ipfsDAO.add(datasourcePath, savedServiceData);

    savedServiceData.setCid(cid);
    serviceDataRepository.save(savedServiceData);

    mintServiceDataService.mint(serviceDataDTO, cid, savedServiceData.getCreationDatetime().toString());

    return savedServiceData;
  }

  ServiceData build(ServiceDataDTO serviceDataDTO) {
    return ServiceData.builder()
        .id(serviceDataDTO.getId().toString())
        .name(serviceDataDTO.getName())
        .description(serviceDataDTO.getDescription())
        .creationDatetime(ZonedDateTime.now(clock))
        .owner(organizationService.getCurrentOrganization().getAddress())
        .services(buildMetadata(serviceDataDTO))
        .build();
  }

  Set<Metadata> buildMetadata(ServiceDataDTO serviceDataDTO) {
    return serviceDataDTO.accept(serviceDataDTOMetadataVisitor).collect(toSet());
  }

  public Optional<ServiceData> findById(String id) {
    return serviceDataRepository.findById(id);
  }

  public ServiceData saveIfNotExists(ServiceData d) {
    return serviceDataRepository.findById(d.getId())
        .orElseGet(() -> serviceDataRepository.save(d));
  }

}
