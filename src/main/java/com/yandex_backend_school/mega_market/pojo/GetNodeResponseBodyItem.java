package com.yandex_backend_school.mega_market.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yandex_backend_school.mega_market.constant.Type;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 19/06/2022 - 9:34 AM
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("childrenPriceSum")
public class GetNodeResponseBodyItem extends GetNodesResponseBodyItem {
  private List<GetNodeResponseBodyItem> children;
  private Integer childrenPriceSum;

  public GetNodeResponseBodyItem(String id, String name, Type type, String parentId, LocalDateTime date, Integer price,
                                 List<GetNodeResponseBodyItem> children, Integer childrenPriceSum) {
    this(id, name, type, parentId, date, price, children);
    this.childrenPriceSum = childrenPriceSum;
  }

  public GetNodeResponseBodyItem(String id, String name, Type type, String parentId, LocalDateTime date, Integer price,
                                 List<GetNodeResponseBodyItem> children) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.parentId = parentId;
    this.date = date;
    this.price = price;
    this.children = children;
  }
}
