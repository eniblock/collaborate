package collaborate.api.datasource.servicedata.nft;

import collaborate.api.datasource.model.AssetId;
import collaborate.api.datasource.model.Nft;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface ServiceDataNftRepository extends JpaRepository<Nft, AssetId>,
    JpaSpecificationExecutor<Nft> {

  Optional<Nft> findOneByNftId(Integer nftId);
}
