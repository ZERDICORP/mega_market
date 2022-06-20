package com.yandex_backend_school.mega_market.exception;

import com.yandex_backend_school.mega_market.constant.Message;
import com.yandex_backend_school.mega_market.pojo.ErrorResponseBody;
import javax.validation.ConstraintViolationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 1:52 PM
 */

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler({ItemNotFoundException.class})
  @ResponseBody
  public ResponseEntity<Object> itemNotFoundException(Exception e) {
    return new ResponseEntity<>(
      new ErrorResponseBody(HttpStatus.NOT_FOUND.value(), Message.ITEM_NOT_FOUND),
      HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    ConversionFailedException.class
  })
  @ResponseBody
  public ResponseEntity<Object> badRequestException(Exception e) {
    return new ResponseEntity<>(
      new ErrorResponseBody(HttpStatus.BAD_REQUEST.value(), Message.VALIDATION_FAILED),
      HttpStatus.BAD_REQUEST);
  }
}
