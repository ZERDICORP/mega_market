package com.yandex_backend_school.mega_market.pojo;

import com.yandex_backend_school.mega_market.constant.Regex;
import com.yandex_backend_school.mega_market.constant.Type;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
public class ImportNodesRequestBodyItem {
  @NotNull
  @Pattern(regexp = "^" + Regex.UUID + "$")
  private String id;
  @Pattern(regexp = "^" + Regex.UUID + "$")
  private String parentId;
  @NotNull
  private Type type;
  @NotBlank
  private String name;
  @Min(0)
  private Integer price;
}
