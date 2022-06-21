package com.yandex_backend_school.mega_market.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yandex_backend_school.mega_market.constant.Type;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 6:16 PM
 */

@Entity(name = "node")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({
  "changes",
  "childrenPriceSum",
  "parentNode",
  "changes"
})
public class Node {
  @Id
  @Column(nullable = false)
  private String id;
  @Column(nullable = false)
  private String name;
  private Integer price;
  @Column(nullable = false)
  private Type type;
  @Column(nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime date;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parentId", referencedColumnName = "id")
  private Node parentNode;

  @OneToMany(mappedBy = "parentNode", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  private Set<Node> children;

  @OneToMany(mappedBy = "node", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  private Set<NodeChange> changes;

  @Transient
  private Integer childrenPriceSum;

  public Node(String id, String name, Integer price, Type type, LocalDateTime date, Node parentNode) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.type = type;
    this.date = date;
    this.parentNode = parentNode;
  }
}
