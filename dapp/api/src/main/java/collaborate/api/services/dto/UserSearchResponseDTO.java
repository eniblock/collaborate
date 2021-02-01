package collaborate.api.services.dto;

import java.util.List;

public class UserSearchResponseDTO {

	private final List<UserDTO> content;
	private final Long total;

	public UserSearchResponseDTO(List<UserDTO> content, Long total) {
		this.content = content;
		this.total = total;
	}

	public List<UserDTO> getContent() {
		return content;
	}

	public Long getTotal() {
		return total;
	}
}
