package com.yandex_backend_school.mega_market.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 20/06/2022 - 4:16 PM
 */

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NodeChangeKey implements Serializable {
  private String nodeId;
  private LocalDateTime date;

  public void setDate(LocalDateTime date) {
    this.date = date;
  }
}
