package com.yandex_backend_school.mega_market.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 20/06/2022 - 4:13 PM
 */

@Entity(name = "node_change")
@Getter
@Setter
@NoArgsConstructor
public class NodeChange {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nodeId", referencedColumnName = "id")
  private Node node;
  @Column(nullable = false)
  private LocalDateTime date;
  private Integer price;

  public NodeChange(Node node, LocalDateTime date, Integer price) {
    this.node = node;
    this.date = date;
    this.price = price;
  }
}
