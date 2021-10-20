package collaborate.api.user.security;

import collaborate.api.user.model.UserDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponseDTO {

  private List<UserDTO> content;
  private Long total;
}
