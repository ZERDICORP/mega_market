package com.yandex_backend_school.mega_market.controller;

import com.yandex_backend_school.mega_market.constant.Regex;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 12:10 PM
 */

@RestController
@Validated
public class NodeController {
  private final NodeService nodeService;

  @Autowired
  public NodeController(NodeService nodeService) {
    this.nodeService = nodeService;
  }

  @GetMapping("/node/{id}/statistic")
  public GetNodesResponseBody getNodeStatistics(@Pattern(regexp = Regex.UUID_ONLY) @PathVariable String id,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                @RequestParam(required = false) LocalDateTime dateStart,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                @RequestParam(required = false) LocalDateTime dateEnd) {
    return nodeService.getNodeChangeStatistics(id, dateStart, dateEnd);
  }

  @GetMapping("/sales")
  public GetNodesResponseBody getSales(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                       @RequestParam LocalDateTime date) {
    return nodeService.getUpdatedIn24HoursOffers(date);
  }

  @DeleteMapping("/delete/{id}")
  public void deleteNode(@Pattern(regexp = Regex.UUID_ONLY) @PathVariable String id) {
    nodeService.deleteNode(id);
  }

  @GetMapping("/nodes/{id}")
  public Node getNode(@Pattern(regexp = Regex.UUID_ONLY) @PathVariable String id) {
    return nodeService.getNode(id);
  }

  @PostMapping("/imports")
  public void importNodes(@Valid @RequestBody ImportNodesRequestBody requestBody) {
    nodeService.importNodes(requestBody);
  }
}
