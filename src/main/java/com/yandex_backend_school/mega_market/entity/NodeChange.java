package com.yandex_backend_school.mega_market.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 20/06/2022 - 4:13 PM
 */

@Entity(name = "node_change")
@IdClass(NodeChangeKey.class)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NodeChange {
  @Id
  @Column(nullable = false)
  private String nodeId;

  @Id
  @Column(nullable = false)
  private LocalDateTime date;

  @Column(nullable = false)
  private Integer price;
}
