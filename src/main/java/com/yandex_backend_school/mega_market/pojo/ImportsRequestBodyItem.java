package com.yandex_backend_school.mega_market.pojo;

import com.yandex_backend_school.mega_market.constant.Type;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 2:16 PM
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportsRequestBodyItem {
  @NotNull
  private UUID id;
  private UUID parentId;
  @NotNull
  private Type type;
  @NotBlank
  private String name;
  private Integer price;
}
