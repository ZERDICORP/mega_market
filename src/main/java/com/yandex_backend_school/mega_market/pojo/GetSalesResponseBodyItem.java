package com.yandex_backend_school.mega_market.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yandex_backend_school.mega_market.constant.Type;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 20/06/2022 - 12:59 PM
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSalesResponseBodyItem {
  protected String id;
  protected String name;
  protected Type type;
  protected String parentId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  protected LocalDateTime date;
  protected Integer price;
}