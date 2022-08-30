package collaborate.api.datasource.businessdata;

import collaborate.api.datasource.model.Nft;
import collaborate.api.datasource.model.assetId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface NftRepository extends JpaRepository<Nft, assetId>,
    JpaSpecificationExecutor<Nft> {

  Optional<Nft> findOneByNftId(Integer nftId);
}
