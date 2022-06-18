package com.yandex_backend_school.mega_market.exception;

import com.yandex_backend_school.mega_market.constant.Message;
import com.yandex_backend_school.mega_market.pojo.ErrorResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 1:52 PM
 */

@ControllerAdvice
public class BadRequestExceptionHandler {
  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<Object> validationFailed(Exception e) {
    return new ResponseEntity<>(
      new ErrorResponseBody(HttpStatus.BAD_REQUEST.value(), Message.VALIDATION_FAILED),
      HttpStatus.BAD_REQUEST);
  }
}
