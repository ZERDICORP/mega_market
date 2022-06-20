package com.yandex_backend_school.mega_market.service;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBodyItem;
import com.yandex_backend_school.mega_market.pojo.GetSalesResponseBody;
import com.yandex_backend_school.mega_market.pojo.GetSalesResponseBodyItem;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.transaction.Transactional;
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

  public GetSalesResponseBody getUpdatedIn24HoursOffers(LocalDateTime date) {
    final List<GetSalesResponseBodyItem> offers = nodeRepository.findUpdatedIn24Hours(Type.OFFER, date)
      .stream()
      .map(n -> new GetSalesResponseBodyItem(
        n.getId(),
        n.getName(),
        n.getType(),
        n.getParentId(),
        n.getDate(),
        n.getPrice()))
      .toList();

    return new GetSalesResponseBody(offers);
  }

  public void deleteNodeTree(Node parentNode) {
    nodeRepository.deleteByParentId(parentNode.getId())
      .stream()
      .filter(n -> n.getType().equals(Type.CATEGORY))
      .forEach(this::deleteNodeTree);

    nodeRepository.delete(parentNode);
  }

  @Transactional
  public void deleteNodeTree(String parentId) {
    final Node parentNode = nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);

    if (parentNode.getType().equals(Type.OFFER)) {
      nodeRepository.delete(parentNode);
      return;
    }

    deleteNodeTree(parentNode);
  }

  public GetNodesResponseBodyItem getNodeTree(Node parentNode) {
    final List<GetNodesResponseBodyItem> nodes = nodeRepository.findByParentId(parentNode.getId())
      .stream()
      .map(n -> {
        if (n.getType().equals(Type.OFFER)) {
          return new GetNodesResponseBodyItem(
            n.getId(),
            n.getName(),
            n.getType(),
            n.getParentId(),
            n.getDate(),
            n.getPrice(),
            null);
        }

        return getNodeTree(n);
      })
      .toList();

    nodes.stream()
      .map(GetNodesResponseBodyItem::getDate)
      .max(LocalDateTime::compareTo)
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

    Integer averagePrice = priceSum / Math.max(1, fullLength.get());
    if (averagePrice == 0) {
      averagePrice = null;
    }

    return new GetNodesResponseBodyItem(
      parentNode.getId(),
      parentNode.getName(),
      parentNode.getType(),
      parentNode.getParentId(),
      parentNode.getDate(),
      averagePrice,
      nodes,
      priceSum);
  }

  public GetNodesResponseBodyItem getNodeTree(String parentId) {
    final Node parentNode = nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);

    if (parentNode.getType().equals(Type.OFFER)) {
      return new GetNodesResponseBodyItem(
        parentNode.getId(),
        parentNode.getName(),
        parentNode.getType(),
        parentNode.getParentId(),
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
