package collaborate.api.user.metadata;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import collaborate.api.tag.model.user.UserMetadataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api/user", name = "tag-user-metadata-client")
interface TagUserMetadataClient {

  @PostMapping(value = "{userId}/metadata", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  UserMetadataDTO upsertMetadata(
      @PathVariable String userId,
      @RequestBody UserMetadataDTO metadata
  );

  @GetMapping(value = "{userId}/metadata", produces = APPLICATION_JSON_VALUE)
  UserMetadataDTO getMetadata(@PathVariable String userId);

  @DeleteMapping(value = "{userId}/metadata", produces = APPLICATION_JSON_VALUE)
  UserMetadataDTO deleteMetadata(@PathVariable String userId);
}
