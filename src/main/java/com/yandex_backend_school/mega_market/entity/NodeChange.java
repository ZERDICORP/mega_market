package com.yandex_backend_school.mega_market.entity;

import java.time.LocalDateTime;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrimaryKeyJoinColumn;
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
  @EmbeddedId
  private NodeChangeKey id;
  @ManyToOne(fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn(name = "nodeId", referencedColumnName = "id")
  @MapsId("nodeId")
  private Node node;
  private Integer price;

  public NodeChange(LocalDateTime date, Node node, Integer price) {
    id = new NodeChangeKey();
    id.setDate(date);

    this.node = node;
    this.price = price;
  }

  public LocalDateTime getDate() {
    return id.getDate();
  }
}
