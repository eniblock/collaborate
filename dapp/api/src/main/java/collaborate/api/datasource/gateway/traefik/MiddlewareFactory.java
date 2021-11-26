package collaborate.api.datasource.gateway.traefik;

import static java.util.stream.Collectors.joining;

import collaborate.api.datasource.gateway.traefik.model.middleware.AddPrefix;
import collaborate.api.datasource.gateway.traefik.model.middleware.Middleware;
import collaborate.api.datasource.gateway.traefik.model.middleware.RedirectRegex;
import collaborate.api.datasource.gateway.traefik.model.middleware.ReplacePathRegex;
import collaborate.api.datasource.gateway.traefik.model.middleware.StripPrefix;
import collaborate.api.datasource.model.dto.web.QueryParam;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MiddlewareFactory {

  public Middleware createStripPrefix(String prefix) {
    return Middleware.builder()
        .stripPrefix(new StripPrefix(List.of(prefix)))
        .build();
  }

  public Middleware createAddPrefix(WebServerResource resource) {
    return Middleware.builder()
        .addPrefix(new AddPrefix(resource.getUrl())).build();
  }

  public Middleware createReplacePathRegex(String regex, String replacement) {
    return Middleware.builder()
        .replacePathRegex(new ReplacePathRegex(regex, replacement))
        .build();
  }

  public Optional<Middleware> createQueryParamOption(
      List<QueryParam> first,
      List<QueryParam> second
  ) {
    List<QueryParam> queryParams = new ArrayList<>();
    Optional<Middleware> queryParamOption = Optional.empty();

    if (first != null) {
      queryParams.addAll(first);
    }
    if (second != null) {
      queryParams.addAll(second);
    }

    if (!queryParams.isEmpty()) {
      String regexReplacement = queryParams.stream()
          .map(entry -> entry.getKey() + "=" + entry.getValue())
          .collect(joining("&", "/${1}?", ""));

      queryParamOption = Optional.of(
          Middleware.builder()
              .redirectRegex(RedirectRegex.builder()
                  .regex("/([^\\?\\s]*)(\\??)(.*)")
                  .replacement(regexReplacement)
                  .build())
              .build()
      );
    }

    return queryParamOption;
  }
}
