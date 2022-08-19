package collaborate.api.datasource.nft;

import collaborate.api.datasource.model.AssetScope;
import collaborate.api.datasource.model.AssetScopeId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetScopeRepository extends JpaRepository<AssetScope, AssetScopeId> {

  Optional<AssetScope> findOneByNftId(Integer nftId);
}
