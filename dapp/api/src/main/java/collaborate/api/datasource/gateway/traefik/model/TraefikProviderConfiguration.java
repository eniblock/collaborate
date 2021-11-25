package collaborate.api.datasource.gateway.traefik.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraefikProviderConfiguration {

  private Http http;

}
