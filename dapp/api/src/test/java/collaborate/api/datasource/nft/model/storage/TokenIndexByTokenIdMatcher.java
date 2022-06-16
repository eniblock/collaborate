package collaborate.api.datasource.nft.model.storage;

import lombok.AllArgsConstructor;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.progress.ThreadSafeMockingProgress;

@AllArgsConstructor
public class TokenIndexByTokenIdMatcher implements ArgumentMatcher<TokenIndex> {

  private Integer tokenId;

  public static TokenIndex matchTokenId(Integer tokenId){
    ThreadSafeMockingProgress.mockingProgress()
        .getArgumentMatcherStorage()
        .reportMatcher(new TokenIndexByTokenIdMatcher(tokenId));
    return null;
  }

  @Override
  public boolean matches(TokenIndex tokenIndex) {
    boolean notNullTokenId =
        tokenIndex.getTokenId() != null && tokenIndex.getTokenId()
            .equals(tokenId);
    boolean nullTokenId = tokenIndex.getTokenId() == null && tokenId == null;
    return nullTokenId || notNullTokenId;
  }
}