package collaborate.api.datasource.servicedata;

import collaborate.api.datasource.servicedata.model.ServiceData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceDataRepository extends JpaRepository<ServiceData, String> {

  Page<ServiceData> findAllByOwner(String owner, Pageable pageable);

}
