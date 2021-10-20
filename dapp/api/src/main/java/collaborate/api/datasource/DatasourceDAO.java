package collaborate.api.datasource;

import static java.util.stream.Collectors.toList;

import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsDAO;
import collaborate.api.ipfs.domain.Link;
import collaborate.api.ipfs.domain.LinkType;
import collaborate.api.ipfs.domain.LsObjectResponse;
import collaborate.api.ipfs.domain.dto.ContentWithCid;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
@Repository
public class DatasourceDAO {

  private final DatasourceProperties datasourceProperties;
  private final DateFormatterFactory dateFormatterFactory;
  private final IpfsDAO ipfsDAO;
  private final ListDatasourceDTOFactory listDatasourceDTOFactory;
  private final TypeReference<Datasource> typedTypeRef = new TypeReference<>() {
  };

  @NonNull
  public List<ContentWithCid<Datasource>> findAll() {
    return streamDatasourcesByCid()
        .collect(toList());
  }

  private Stream<ContentWithCid<Datasource>> streamDatasourcesByCid() {
    String rootCid = ipfsDAO.addDirectory(datasourceProperties.getRootFolder());

    return ipfsDAO.listDirectoryContent(rootCid).getObjects().stream()
        // Get children folder hash inside the root folder
        .map(LsObjectResponse::getLinks)
        .flatMap(List::stream)
        .filter(l -> LinkType.DIRECTORY.equals(l.getType()))
        .map(Link::getHash)
        // Convert files in children folders
        .flatMap(this::listDirectChildrenHashes)
        .map(h -> new ContentWithCid<>(h, ipfsDAO.cat(h, typedTypeRef)))
        .filter(e -> Objects.nonNull(e.getContent()));
  }

  private Stream<String> listDirectChildrenHashes(String hash) {
    return ipfsDAO.listDirectoryContent(hash).getObjects().stream()
        .map(LsObjectResponse::getLinks)
        .flatMap(List::stream)
        .filter(l -> LinkType.FILE.equals(l.getType()))
        .map(Link::getHash);
  }

  @NonNull
  public Page<ListDatasourceDTO> findAll(Pageable pageable) {
    var datasource =
        streamDatasourcesByCid()
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .map(ContentWithCid::getContent)
            .map(listDatasourceDTOFactory::create)
            .filter(Objects::nonNull)
            .collect(toList());
    return new PageImpl<>(datasource, pageable, streamDatasourcesByCid().count());
  }


  @NonNull
  public List<ContentWithCid<Datasource>> findAllById(@NonNull Iterable<String> ids) {
    return streamDatasourcesByCid()
        .filter(d -> CollectionUtils.contains(ids.iterator(), d.getContent().getId()))
        .collect(toList());
  }

  @NonNull
  public Optional<ContentWithCid<Datasource>> findById(@NonNull String id) {
    return streamDatasourcesByCid()
        .filter(e -> StringUtils.equals(id, e.getContent().getId()))
        .findFirst();
  }

  public long count() {
    return streamDatasourcesByCid().count();
  }

  public ContentWithCid<Datasource> save(Datasource datasource) throws IOException {
    var datasourcePath = Path.of(datasourceProperties.getRootFolder(),
        dateFormatterFactory.forPattern(datasourceProperties.getPartitionDatePattern()),
        datasource.getId()
    );
    var cid = ipfsDAO.add(datasourcePath, datasource);
    return new ContentWithCid<>(cid, datasource);
  }
}
