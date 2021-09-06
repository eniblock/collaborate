package collaborate.api.passport.find;

import collaborate.api.tag.model.storage.IndexerQueryResponse;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.core.type.TypeReference;

public class FindPassportFeatures {

  private FindPassportFeatures() {
  }

  public static final String findPassportsIdByVoJsonResponseWithError = TestResources
      .read("/passport/find/sc.storage.findPassportsIdByVo.response.with.error.json");

  public static final String findPassportsByIdsJsonResponseWithEmptyPassportMetadataTokenById = TestResources
      .read("/passport/find/sc.storage.findPassportsByIds.response.with.empty.passportMetadataByTokenId.json");

  public static final IndexerQueryResponse<Integer> findPassportsIdByVoResponse = TestResources.read(
      findPassportsIdByVoJsonResponseWithError,
      new TypeReference<>() {
      }
  );

  public static final PassportByIdsDTO findPassportsByIdsResponseWithEmptyPassportMetadataTokenById = TestResources.read(
      findPassportsByIdsJsonResponseWithEmptyPassportMetadataTokenById,
      PassportByIdsDTO.class
  );
}
