package app.foot.repository.entity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ExceptionOutput {

  // handling specific exception
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ExceptionTypt> NotFoundExceptionHandling(NotFoundException exception, WebRequest request){
    ExceptionTypt ExceptionTypt =
            new ExceptionTypt(new Date(), exception.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(ExceptionTypt, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ExceptionTypt> BadRequestExceptionHandling(BadRequestException exception, WebRequest request){
    ExceptionTypt ExceptionTypt =
            new ExceptionTypt(new Date(), exception.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(ExceptionTypt, HttpStatus.BAD_REQUEST);
  }

  // handling global exception
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionTypt> GlobalExceptionHandling(Exception exception, WebRequest request){
    ExceptionTypt ExceptionTypt =
            new ExceptionTypt(new Date(), exception.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(ExceptionTypt, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}