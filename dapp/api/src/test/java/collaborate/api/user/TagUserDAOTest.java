package collaborate.api.user;

import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.tag.model.user.UsersDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TagUserDAOTest {

  @Mock
  private TagUserClient tagUserClient;

  @InjectMocks
  private TagUserDAO tagUserDAO;

  @Test
  void clean_shouldConvertStringAsExpected() {
    // GIVEN
    String email = "asset@owner.net";
    String expected = "asset_._xdev-at_._owner.net";
    // WHEN
    String actual = tagUserDAO.cleanUserId(email);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void unclean_shouldConvertStringAsExpected() {
    // GIVEN
    String toUnclean = "asset_._xdev-at_._owner.net";
    String expected = "asset@owner.net";
    // WHEN
    String actual = tagUserDAO.uncleanUserId(toUnclean);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void cleanThenUnclean_shouldBeIdentity() {
    // GIVEN
    String email = "asset@owner.net";
    // WHEN
    String actual = tagUserDAO.uncleanUserId(tagUserDAO.cleanUserId(email));
    // THEN
    assertThat(actual).isEqualTo(email);
  }

  @Test
  void create_shouldDeserialize_withTagJsonResponse() throws JsonProcessingException {
    // GIVEN
    var type = objectMapper.getTypeFactory()
        .constructCollectionType(List.class, UserWalletDTO.class);
    String userId = "userId";
    // WHEN
    List<UserWalletDTO> createResult = objectMapper
        .readValue(UserWalletFeature.tagUserJsonResponse, type);
    when(tagUserClient.create(any())).thenReturn(
        new ResponseEntity<>(createResult, HttpStatus.CREATED));

    var actualResult = tagUserDAO.create(userId);
    // THEN
    // create should return expected type
    tagUserClient.create(new UsersDTO());
    assertThat(actualResult)
        .isPresent()
        .contains(UserWalletFeature.userWallet);
  }
}
