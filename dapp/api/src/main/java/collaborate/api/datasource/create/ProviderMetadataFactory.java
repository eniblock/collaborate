package collaborate.api.datasource.create;

import static java.util.stream.Collectors.toSet;

import collaborate.api.datasource.model.Attribute;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.traefik.routing.RoutingKeyFromKeywordSupplier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProviderMetadataFactory {

  public static final int NAME_GROUP_INDEX = 1;
  public static final int VALUE_GROUP_INDEX = 2;
  public static final int TYPE_GROUP_INDEX = 3;
  private static final String METADATA_PREFIX = "metadata:";
  private static final String KEY_GROUP_REGEXP = "((?:[A-z]*(?:\\.|-[A-z])*)*)";
  private static final String VALUE_GROUP_REGEXP = ":([^:]*)";
  private static final String OPTIONAL_TYPE_GROUP_REGEXP = "(?::([A-z]*))?";
  public static final Pattern METADATA_REGEXP =
      Pattern.compile(
          METADATA_PREFIX + KEY_GROUP_REGEXP + VALUE_GROUP_REGEXP + OPTIONAL_TYPE_GROUP_REGEXP);
  public static final String DATASOURCE_PURPOSE = "datasource:purpose";

  private final AuthenticationProviderMetadataVisitor authenticationProviderMetadataVisitor;
  private final ObjectMapper objectMapper;

  public Set<Attribute> from(DatasourceDTO datasource) {
    var authAttributes = datasource.getAuthMethod().accept(authenticationProviderMetadataVisitor);
    var resAttributes = from((WebServerDatasourceDTO) datasource);
    resAttributes.add(Attribute.builder()
        .name("datasource:authentication")
        .value(datasource.getAuthMethod().getClass().getSimpleName())
        .type("string")
        .build()
    );
    if (datasource.getAuthMethod() instanceof CertificateBasedBasicAuth) {
      resAttributes.add(Attribute.builder()
          .name("datasource:CertificateBasedBasicAuth:caEmail")
          .value(((CertificateBasedBasicAuth) datasource.getAuthMethod()).getCaEmail())
          .type("string")
          .build()
      );
    }

    if (datasource instanceof WebServerDatasourceDTO) {
      return Sets.union(authAttributes, resAttributes);
    }
    throw new NotImplementedException();
  }

  private Set<Attribute> from(WebServerDatasourceDTO datasourceDTO) {
    Attribute keywordAttribute;
    try {
      keywordAttribute =
          new Attribute(
              DATASOURCE_PURPOSE,
              objectMapper.writeValueAsString(datasourceDTO.getKeywords()),
              "string[]");
    } catch (JsonProcessingException e) {
      log.error("cannot serialize datasource keywords {}", datasourceDTO.getKeywords());
      throw new IllegalStateException(e);
    }
    var resourceAttributes =
        datasourceDTO.getResources().stream()
            .map(WebServerResource::getKeywords)
            .map(this::createAttributes)
            .flatMap(Set::stream)
            .collect(toSet());
    resourceAttributes.add(keywordAttribute);
    return resourceAttributes;
  }

  Set<Attribute> createAttributes(Collection<String> keywords) {
    var routingKey = new RoutingKeyFromKeywordSupplier(keywords).get();
    return keywords.stream()
        .map(METADATA_REGEXP::matcher)
        .map(matcher -> createAttributes(matcher, routingKey))
        .flatMap(Optional::stream)
        .collect(toSet());
  }

  Optional<Attribute> createAttributes(Matcher matcher, String routingKey) {
    Optional<Attribute> attributeOpt;
    if (matcher.find()) {
      attributeOpt =
          Optional.of(
              Attribute.builder()
                  .name(routingKey + ":" + matcher.group(NAME_GROUP_INDEX))
                  .value(matcher.group(VALUE_GROUP_INDEX))
                  .type(matcher.group(TYPE_GROUP_INDEX))
                  .build());
    } else {
      attributeOpt = Optional.empty();
    }
    return attributeOpt;
  }
}
