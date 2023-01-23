package app.foot.repository.entity.exception;

public class TooManyRequestsException extends RuntimeException {
  public TooManyRequestsException(String message) {
    super(message);
  }

  public TooManyRequestsException(Exception source) {
    super(source);
  }
}
