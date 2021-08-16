package collaborate.api.datasource.repository;

import collaborate.api.datasource.domain.web.WebServerResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebServerResourceRepository extends JpaRepository<WebServerResource,Long> {
}
