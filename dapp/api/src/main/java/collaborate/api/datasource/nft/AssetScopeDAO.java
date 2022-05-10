package collaborate.api.datasource.nft;

import collaborate.api.datasource.model.scope.AssetScope;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetScopeDAO extends JpaRepository<AssetScope, String> {


}
