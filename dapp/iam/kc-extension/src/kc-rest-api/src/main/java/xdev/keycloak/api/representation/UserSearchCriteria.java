package xdev.keycloak.api.representation;

import java.util.List;

import javax.ws.rs.QueryParam;

public class UserSearchCriteria {

	@QueryParam("search")
	private String search;
	@QueryParam("email")
	private String email;
	@QueryParam("roles")
	private List<String> roles;
	@QueryParam("accountId")
	private Long accountId;

	// Pagination
	@QueryParam("paged")
	private boolean paged;
	@QueryParam("pageNumber")
	private Integer pageNumber;
	@QueryParam("pageSize")
	private Integer pageSize;
	@QueryParam("offset")
	private Long offset;

	// Sorting
	@QueryParam("sorted")
	private boolean sorted;
	@QueryParam("sort")
	private List<String> sort;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public boolean isPaged() {
		return paged;
	}

	public void setPaged(boolean paged) {
		this.paged = paged;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}

	public List<String> getSort() {
		return sort;
	}

	public void setSort(List<String> sort) {
		this.sort = sort;
	}
}
