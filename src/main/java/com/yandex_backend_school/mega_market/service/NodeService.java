package com.yandex_backend_school.mega_market.service;

import com.yandex_backend_school.mega_market.constant.DateTimeTemplate;
import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.entity.NodeChange;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.pojo.GetNodeResponseBodyItem;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBodyItem;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.NodeChangeRepository;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
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
  private final NodeChangeRepository nodeChangeRepository;

  @Autowired
  public NodeService(NodeRepository nodeRepository, NodeChangeRepository nodeChangeRepository) {
    this.nodeRepository = nodeRepository;
    this.nodeChangeRepository = nodeChangeRepository;
  }

  public GetNodesResponseBody getNodeChangeStatistics(String id, LocalDateTime dateStart, LocalDateTime dateEnd) {
    final Node node = nodeRepository.findById(id)
      .orElseThrow(ItemNotFoundException::new);

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    final List<GetNodesResponseBodyItem> changes = nodeChangeRepository.findByNodeIdInDateRange(
        id,
        Optional.ofNullable(dateStart).orElse(LocalDateTime.parse(DateTimeTemplate.MIN, formatter)),
        Optional.ofNullable(dateEnd).orElse(LocalDateTime.parse(DateTimeTemplate.MAX, formatter)))
      .stream()
      .map(c -> new GetNodesResponseBodyItem(
        node.getId(),
        node.getName(),
        node.getType(),
        node.getParentId(),
        c.getDate(),
        c.getPrice()))
      .toList();

    return new GetNodesResponseBody(changes);
  }

  public GetNodesResponseBody getUpdatedIn24HoursOffers(LocalDateTime date) {
    final List<GetNodesResponseBodyItem> offers = nodeRepository.findUpdatedIn24Hours(Type.OFFER, date)
      .stream()
      .map(n -> new GetNodesResponseBodyItem(
        n.getId(),
        n.getName(),
        n.getType(),
        n.getParentId(),
        n.getDate(),
        n.getPrice()))
      .toList();

    return new GetNodesResponseBody(offers);
  }

  public void deleteNode(Node parentNode) {
    final List<Node> deletedNodes = nodeRepository.deleteByParentId(parentNode.getId());
    final List<String> deletedNodeIds = Stream.concat(deletedNodes.stream(), Stream.of(parentNode))
      .map(Node::getId)
      .toList();

    deletedNodes
      .stream()
      .filter(n -> n.getType().equals(Type.CATEGORY))
      .forEach(this::deleteNode);

    nodeChangeRepository.deleteAllByNodeIdInBatch(deletedNodeIds);
    nodeRepository.delete(parentNode);
  }

  @Transactional
  public void deleteNode(String parentId) {
    final Node parentNode = nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);

    if (parentNode.getType().equals(Type.OFFER)) {
      nodeRepository.delete(parentNode);
      return;
    }

    deleteNode(parentNode);
  }

  public GetNodeResponseBodyItem getNode(Node parentNode) {
    final List<GetNodeResponseBodyItem> nodes = nodeRepository.findByParentId(parentNode.getId())
      .stream()
      .map(n -> {
        if (n.getType().equals(Type.OFFER)) {
          return new GetNodeResponseBodyItem(
            n.getId(),
            n.getName(),
            n.getType(),
            n.getParentId(),
            n.getDate(),
            n.getPrice(),
            null);
        }

        return getNode(n);
      })
      .toList();

    nodes.stream()
      .map(GetNodeResponseBodyItem::getDate)
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

    return new GetNodeResponseBodyItem(
      parentNode.getId(),
      parentNode.getName(),
      parentNode.getType(),
      parentNode.getParentId(),
      parentNode.getDate(),
      averagePrice,
      nodes,
      priceSum);
  }

  public GetNodeResponseBodyItem getNode(String parentId) {
    final Node parentNode = nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);

    if (parentNode.getType().equals(Type.OFFER)) {
      return new GetNodeResponseBodyItem(
        parentNode.getId(),
        parentNode.getName(),
        parentNode.getType(),
        parentNode.getParentId(),
        parentNode.getDate(),
        parentNode.getPrice(),
        null);
    }

    // If the node is a category, then we need to look for possible
    // children for it, since we need to calculate their average
    // price, and if one of the children is also a category, do the
    // same for this child.
    return getNode(parentNode);
  }

  public void importNodes(ImportNodesRequestBody requestBody) {
    final LocalDateTime updateDate = requestBody.getUpdateDate();
    final List<ImportNodesRequestBodyItem> items = requestBody.getItems();

    // Sorting items so that categories come first, then offers.
    items.sort(Comparator.comparingInt(n -> n.getType().ordinal()));

    items.forEach(n -> {
      nodeRepository.save(new Node(
        n.getId(),
        n.getName(),
        n.getParentId(),
        n.getPrice(),
        n.getType(),
        updateDate));

      if (n.getType().equals(Type.OFFER)) {
        nodeChangeRepository.save(new NodeChange(
          n.getId(),
          updateDate,
          n.getPrice()));

        nodeRepository.findById(n.getParentId())
          .ifPresent(pn -> nodeChangeRepository.save(new NodeChange(
            pn.getId(),
            updateDate,
            // On the line below, I call the getNode method to
            // recursively find out the average price of all
            // possible children of the current category.
            getNode(pn).getPrice())));
      }
    });
  }
}
