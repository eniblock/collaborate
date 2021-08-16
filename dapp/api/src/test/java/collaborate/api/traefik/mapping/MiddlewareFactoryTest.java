package collaborate.api.traefik.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.domain.web.QueryParam;
import collaborate.api.traefik.domain.entrypoint.middleware.Middleware;
import collaborate.api.traefik.domain.entrypoint.middleware.RedirectRegex;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

class MiddlewareFactoryTest {

  private static final String REGEX = "/([^\\?\\s]*)(\\??)(.*)";
  MiddlewareFactory middlewareFactory = new MiddlewareFactory();

  @Test
  void createQueryParam_shouldReturnEmtpy_withNullParameters() {
    // GIVEN
    // WHEN
    var actualQueryParamsOpt = middlewareFactory.createQueryParamOption(null, null);
    // THEN
    assertThat(actualQueryParamsOpt).isNotPresent();
  }

  @Test
  void createQueryParam_shouldReturnEmpty_withEmptyParameters() {
    // GIVEN
    List<QueryParam> first = Lists.emptyList();
    List<QueryParam> second = Lists.emptyList();
    // WHEN
    var actualQueryParamsOpt = middlewareFactory.createQueryParamOption(first, second);
    // THEN
    assertThat(actualQueryParamsOpt).isNotPresent();
  }

  @Test
  void createQueryParam_shouldReturnEmpty_withNonEmptyParameters() {
    // GIVEN
    List<QueryParam> first = List.of(QueryParam.builder().key("k1").value("v1").build());
    List<QueryParam> second = List.of(
        QueryParam.builder().key("k2").value("v2").build(),
        QueryParam.builder().key("k3").value("v3").build()
    );
    // WHEN
    var actualQueryParamsOpt = middlewareFactory.createQueryParamOption(first, second);
    // THEN
    assertThat(actualQueryParamsOpt).isEqualTo(
        Optional.of(Middleware.builder().redirectRegex(
            RedirectRegex.builder()
                .regex(REGEX)
                .replacement("/${1}?k1=v1&k2=v2&k3=v3")
                .build()
            ).build()
        )
    );
  }


}
