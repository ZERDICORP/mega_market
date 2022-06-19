package com.yandex_backend_school.mega_market.controller;

import com.yandex_backend_school.mega_market.constant.Regex;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.service.NodeService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @GetMapping("/nodes/{id:" + Regex.UUID + "}")
  public GetNodesResponseBody getNode(@PathVariable String id) {
    return nodeService.getNodeTree(id);
  }

  @PostMapping("/imports")
  public void importNodes(@Valid @RequestBody ImportNodesRequestBody requestBody) {
    nodeService.importNodes(requestBody);
  }
}
