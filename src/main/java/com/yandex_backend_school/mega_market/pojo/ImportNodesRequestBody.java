package com.yandex_backend_school.mega_market.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.yandex_backend_school.mega_market.deserializer.DateDeserializer;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
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
  @JsonDeserialize(using = DateDeserializer.class)
  private Date updateDate;
}
