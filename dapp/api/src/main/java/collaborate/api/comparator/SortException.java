package collaborate.api.comparator;

public class SortException extends RuntimeException {

  public SortException(String message) {
    super(message);
  }

  public SortException(Throwable e) {
    super(e);
  }

  public SortException(String message, Throwable e) {
    super(message, e);
  }
}
