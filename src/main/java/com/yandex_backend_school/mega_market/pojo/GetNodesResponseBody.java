package com.yandex_backend_school.mega_market.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yandex_backend_school.mega_market.constant.Type;
import java.util.Date;
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
public class GetNodesResponseBody {
  private String id;
  private String name;
  private Type type;
  private String parentId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private Date date;
  private Integer price;
  private List<GetNodesResponseBody> children;
  private Integer childrenPriceSum;

  public GetNodesResponseBody(String id, String name, Type type, String parentId, Date date, Integer price,
                              List<GetNodesResponseBody> children) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.parentId = parentId;
    this.date = date;
    this.price = price;
    this.children = children;
  }
}
