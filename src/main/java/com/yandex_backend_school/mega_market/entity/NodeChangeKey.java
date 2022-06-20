package com.yandex_backend_school.mega_market.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 20/06/2022 - 4:16 PM
 */

public class NodeChangeKey implements Serializable {
  private String nodeId;
  private LocalDateTime date;
}
