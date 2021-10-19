package collaborate.api.user.security;

import collaborate.api.user.model.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponseDTO {

  private List<UserDTO> content;
  private Long total;
}
