package collaborate.api.datasource.businessdata;

import collaborate.api.datasource.model.NFTScopeId;
import collaborate.api.datasource.model.Nft;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface NftRepository extends JpaRepository<Nft, NFTScopeId>,
    JpaSpecificationExecutor<Nft> {

  Optional<Nft> findOneByNftId(Integer nftId);
}
