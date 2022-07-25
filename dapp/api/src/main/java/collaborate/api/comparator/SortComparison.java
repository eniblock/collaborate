package collaborate.api.comparator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class SortComparison {

  public <T> Optional<Comparator<T>> sortComparator(Class<T> cls, Sort sort) {
    return sort.stream()
        .map(s -> {
          var c = newMethodComparator(cls, s.getProperty());
          if (s.getDirection().isDescending()) {
            return c.reversed();
          }
          return c;
        })
        .reduce(Comparator::thenComparing);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T> Comparator<T> newMethodComparator(Class<T> cls, String attributeName) {
    Method method;
    try {
      method = cls.getMethod("get" + StringUtils.capitalize(attributeName));
    } catch (NoSuchMethodException e) {
      throw new SortException("No property accessor for '" + attributeName + "'", e);
    }
    if (method.getParameterTypes().length != 0) {
      throw new SortException("Property accessor for" + attributeName + " takes parameters");
    }

    Class<?> returnType = method.getReturnType();
    if (!Comparable.class.isAssignableFrom(returnType)) {
      throw new SortException("Property " + attributeName + " has a return " + returnType
          + " type which is not Comparable");
    }

    return newMethodComparator(method,
        (Class<? extends Comparable>) returnType);
  }

  private <T, R extends Comparable<R>> Comparator<T> newMethodComparator(final Method method,
      final Class<R> returnType) {
    return new Comparator<>() {
      @Override
      public int compare(T o1, T o2) {
        try {
          R a = invoke(method, o1);
          R b = invoke(method, o2);
          // Keep null at the beginning
          if (a == null && b == null) {
            return 0;
          }
          if (a == null) {
            return -1;
          }
          if (b == null) {
            return 1;
          }
          return a.compareTo(b);
        } catch (Exception e) {
          throw new SortException(e);
        }
      }

      private R invoke(Method method, T o)
          throws InvocationTargetException, IllegalAccessException {
        return returnType.cast(method.invoke(o));
      }
    };
  }

  public <T> Stream<T> sorted(Stream<T> straemToSort, Sort sort, Class<T> tClass) {
    return sortComparator(tClass, sort)
        .map(straemToSort::sorted)
        .orElse(straemToSort);
  }
}
