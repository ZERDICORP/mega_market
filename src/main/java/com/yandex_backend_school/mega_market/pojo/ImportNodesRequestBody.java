package com.yandex_backend_school.mega_market.pojo;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 12:15 PM
 */

@Getter
@Setter
@Validated
public class ImportNodesRequestBody {
  @NotEmpty
  @NotNull
  private List<@Valid ImportNodesRequestBodyItem> items;

  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime updateDate;
}
