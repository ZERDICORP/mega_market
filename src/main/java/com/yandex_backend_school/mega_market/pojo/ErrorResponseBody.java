package com.yandex_backend_school.mega_market.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 1:36 PM
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorResponseBody {
  private Integer code;
  private String message;
}
