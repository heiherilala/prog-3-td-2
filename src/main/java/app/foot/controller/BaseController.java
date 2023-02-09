package app.foot.controller;

import app.foot.controller.rest.RestErrorFormat;
import app.foot.exception.BadRequestException;
import app.foot.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class BaseController {

  @ExceptionHandler(value = {NotFoundException.class})
  ResponseEntity<RestErrorFormat> handleNotFound(
          NotFoundException e) {
    log.info("Not found", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(value = {Exception.class})
  ResponseEntity<RestErrorFormat> handleDefault(Exception e) {
    log.error("Internal error", e);
    return new ResponseEntity<>(
            toRest(e, HttpStatus.INTERNAL_SERVER_ERROR),
            HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {BadRequestException.class})
  ResponseEntity<RestErrorFormat> handleBadRequest(
      BadRequestException e) {
    log.info("Bad request", e);
    return new ResponseEntity<>(toRest(e, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler(value = {MissingServletRequestParameterException.class})
  ResponseEntity<RestErrorFormat> handleBadRequest(
      MissingServletRequestParameterException e) {
    log.info("Missing parameter", e);
    return handleBadRequest(new BadRequestException(e.getMessage()));
  }

  @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
  ResponseEntity<RestErrorFormat> handleConversionFailed(
      MethodArgumentTypeMismatchException e) {
    log.info("Conversion failed", e);
    String message = e.getCause().getCause().getMessage();
    return handleBadRequest(new BadRequestException(message));
  }


  private RestErrorFormat toRest(Exception e, HttpStatus status) {
    var ErrorFormated = new RestErrorFormat();
    ErrorFormated.setTime(Instant.now());
    ErrorFormated.setStatus(status.toString());
    ErrorFormated.setMessage(e.getMessage());
    return ErrorFormated;
  }

}
