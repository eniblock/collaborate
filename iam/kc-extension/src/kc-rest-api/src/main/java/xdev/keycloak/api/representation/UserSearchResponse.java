package xdev.keycloak.api.representation;

import java.util.List;

public class UserSearchResponse {

	private final List<UserRepresentation> content;
	private final Long total;

	public UserSearchResponse(List<UserRepresentation> content, Long total) {
		this.content = content;
		this.total = total;
	}

	public List<UserRepresentation> getContent() {
		return content;
	}

	public Long getTotal() {
		return total;
	}
}
