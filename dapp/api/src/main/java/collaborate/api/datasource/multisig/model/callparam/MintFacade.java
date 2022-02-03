package collaborate.api.datasource.multisig.model.callparam;

import collaborate.api.datasource.multisig.model.TransactionBuildParam;
import collaborate.api.datasource.multisig.model.callparam.mint.Mint;
import collaborate.api.datasource.multisig.model.callparam.mint.MintParams;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MintFacade {

  private TransactionBuildParam transactionBuildParam;

  public Mint getMint() {
    return transactionBuildParam.
        getCallParams()
        .getParameters()
        .getMint();
  }

  public MintParams getMintParams() {
    return transactionBuildParam.
        getCallParams()
        .getParameters()
        .getMint()
        .getMintParams();
  }

  public String getOwner() {
    return getMintParams().getAddress();
  }

  public String getOperator() {
    return getMint().getOperator();
  }

  public String getIpfsMetadataURI() {
    return getMintParams().getIpfsMetadata().toString();
  }
}
