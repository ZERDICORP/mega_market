package com.yandex_backend_school.mega_market.entity;

import com.yandex_backend_school.mega_market.constant.Type;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 6:16 PM
 */

@Entity(name = "node")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Node {
  @Id
  @Column(nullable = false)
  private String id;
  @Column(nullable = false)
  private String name;
  private String parentId;
  private Integer price;
  @Column(nullable = false)
  private Type type;
  @Column(nullable = false)
  private Date date;
}
