package collaborate.api.organization.tag;

import collaborate.api.restclient.tag.TagMap;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationsResponse {

  @JsonProperty("organizations")
  private TagMap<String, OrganizationDTO> organizationByPublicKeyHashes;

}
