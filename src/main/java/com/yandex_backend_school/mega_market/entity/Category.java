package com.yandex_backend_school.mega_market.entity;

import java.util.UUID;
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

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Category {
  @Id
  @Column(nullable = false, columnDefinition = "UUID")
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "UUID")
  private UUID parentId;
}
