package collaborate.api.user.security;

import java.util.List;
import lombok.Data;

@Data
public class UserSearchCriteria {

  // Search
  private String search;
  private String email;
  private List<String> roles;
  private Long accountId;

  // Pagination
  private boolean paged = false;
  private Integer pageNumber;
  private Integer pageSize;
  private Long offset;

  // Sorting
  private boolean sorted = false;
  private List<String> sort;

}
