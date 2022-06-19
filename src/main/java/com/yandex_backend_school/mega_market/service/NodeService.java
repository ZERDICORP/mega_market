package com.yandex_backend_school.mega_market.service;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 12:48 PM
 */

@Service
public class NodeService {
  private final NodeRepository nodeRepository;

  @Autowired
  public NodeService(NodeRepository nodeRepository) {
    this.nodeRepository = nodeRepository;
  }

  public GetNodesResponseBody getNodeTree(Node parentNode) {
    final List<GetNodesResponseBody> nodes = nodeRepository.findAllByParentId(parentNode.getId())
      .stream()
      .map(n -> {
        if (n.getType().equals(Type.OFFER)) {
          return new GetNodesResponseBody(
            n.getId(),
            n.getName(),
            n.getType(),
            n.getParent_id(),
            n.getDate(),
            n.getPrice(),
            null);
        }

        return getNodeTree(n);
      })
      .toList();

    nodes.stream()
      .map(GetNodesResponseBody::getDate)
      .max(Date::compareTo)
      .ifPresent(parentNode::setDate);

    final AtomicInteger fullLength = new AtomicInteger(nodes.size());
    final int priceSum = nodes.stream()
      .mapToInt(n -> {
        if (n.getType().equals(Type.CATEGORY)) {
          fullLength.addAndGet(n.getChildren().size() - 1);
          return n.getChildrenPriceSum();
        }
        return n.getPrice();
      })
      .reduce(0, Integer::sum);

    return new GetNodesResponseBody(
      parentNode.getId(),
      parentNode.getName(),
      parentNode.getType(),
      parentNode.getParent_id(),
      parentNode.getDate(),
      priceSum / Math.max(1, fullLength.get()),
      nodes,
      priceSum);
  }

  public GetNodesResponseBody getNodeTree(String parentId) {
    final Node parentNode = nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);

    if (parentNode.getType().equals(Type.OFFER)) {
      return new GetNodesResponseBody(
        parentNode.getId(),
        parentNode.getName(),
        parentNode.getType(),
        parentNode.getParent_id(),
        parentNode.getDate(),
        parentNode.getPrice(),
        null);
    }

    return getNodeTree(parentNode);
  }

  public void importNodes(ImportNodesRequestBody requestBody) {
    final List<Node> requestBodyItems = requestBody.getItems().stream()
      .map(i -> new Node(
        i.getId(),
        i.getName(),
        i.getParentId(),
        i.getPrice(),
        i.getType(),
        requestBody.getUpdateDate()))
      .toList();

    nodeRepository.saveAll(requestBodyItems);
  }
}