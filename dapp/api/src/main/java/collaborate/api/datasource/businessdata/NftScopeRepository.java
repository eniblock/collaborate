package collaborate.api.datasource.businessdata;

import collaborate.api.datasource.model.NFTScopeId;
import collaborate.api.datasource.model.NftScope;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface NftScopeRepository extends JpaRepository<NftScope, NFTScopeId> {

  Optional<NftScope> findOneByNftId(Integer nftId);
}
