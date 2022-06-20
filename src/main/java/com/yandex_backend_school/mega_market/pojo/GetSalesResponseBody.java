package com.yandex_backend_school.mega_market.pojo;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 20/06/2022 - 1:04 PM
 */

@Getter
@Setter
@NoArgsConstructor
public class GetSalesResponseBody {
  private List<GetSalesResponseBodyItem> items;

  public GetSalesResponseBody(List<GetSalesResponseBodyItem> items) {
    this.items = items;
  }
}
