package com.yandex_backend_school.mega_market.service;

import com.yandex_backend_school.mega_market.constant.DateTimeTemplate;
import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.entity.NodeChange;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBodyItem;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.NodeChangeRepository;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 12:48 PM
 */

@Service
public class NodeService {
  private final NodeRepository nodeRepository;
  private final NodeChangeRepository nodeChangeRepository;
  private final EntityManager entityManager;

  @Autowired
  public NodeService(NodeRepository nodeRepository, NodeChangeRepository nodeChangeRepository, EntityManager entityManager) {
    this.nodeRepository = nodeRepository;
    this.nodeChangeRepository = nodeChangeRepository;
    this.entityManager = entityManager;
  }

  public GetNodesResponseBody getNodeChangeStatistics(String id, LocalDateTime dateStart, LocalDateTime dateEnd) {
    final Node node = nodeRepository.findById(id)
      .orElseThrow(ItemNotFoundException::new);

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    final List<GetNodesResponseBodyItem> changes = nodeChangeRepository.findByNodeIdInDateRange(
        id,
        // The construct on the line below is just a replacement for
        // the following construct:
        // dateStart == null ? (...) : (...);
        Optional.ofNullable(dateStart).orElse(LocalDateTime.parse(DateTimeTemplate.MIN, formatter)),
        Optional.ofNullable(dateEnd).orElse(LocalDateTime.parse(DateTimeTemplate.MAX, formatter)))
      .stream()
      .map(c -> new GetNodesResponseBodyItem(
        node.getId(),
        node.getName(),
        node.getType(),
        node.getParentNode().getId(),
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
        n.getParentNode().getId(),
        n.getDate(),
        n.getPrice()))
      .toList();

    return new GetNodesResponseBody(offers);
  }

  @Transactional
  public void deleteNode(String parentId) {
    final Node parentNode = nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);

    nodeRepository.delete(parentNode);
    nodeRepository.flush();

    updateParentNode(parentNode.getParentNode(), LocalDateTime.now());
  }

  public Node getNode(String parentId) {
    return nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);
  }

  @Transactional
  public void importNodes(ImportNodesRequestBody requestBody) {
    final LocalDateTime updateDate = requestBody.getUpdateDate();
    final List<ImportNodesRequestBodyItem> items = requestBody.getItems();
    final LocalDateTime start = LocalDateTime.now();

    // Sorting items so that categories come first, then offers.
    items.sort(Comparator.comparingInt(n -> n.getType().ordinal()));
    items.forEach(i -> saveNode(i,
      updateDate.plus(Duration.between(LocalDateTime.now(), start))));
  }

  public void saveNode(ImportNodesRequestBodyItem item, LocalDateTime updateDate) {
    Node parentNode = null;
    if (item.getParentId() != null) {
      entityManager.clear();
      parentNode = nodeRepository.findById(item.getParentId())
        .orElse(null);
    }

    final Node node = new Node(
      item.getId(),
      item.getName(),
      item.getPrice(),
      item.getType(),
      updateDate,
      parentNode);

    nodeRepository.saveAndFlush(node);

    if (item.getType().equals(Type.OFFER)) {
      // We save the state of the node (price, date) for the
      // subsequent collection of statistics.
      nodeChangeRepository.saveAndFlush(new NodeChange(
        updateDate,
        node,
        item.getPrice()));

      updateParentNode(parentNode, updateDate);
      return;
    }

    entityManager.clear();
    nodeRepository.findById(node.getId())
      .ifPresent(n -> updateParentNode(n, updateDate));
  }

  public void updateParentNode(Node parentNode, LocalDateTime updateDate) {
    if (parentNode == null) {
      return;
    }

    // Calculate the new average price of all child nodes
    // recursively.
    collectChildrenData(parentNode);

    nodeRepository.saveAndFlush(parentNode);
    nodeChangeRepository.saveAndFlush(new NodeChange(
      updateDate,
      parentNode,
      Optional.ofNullable(parentNode.getPrice()).orElse(0)));

    if (parentNode.getParentNode() != null) {
      updateParentNode(parentNode.getParentNode(), updateDate);
    }
  }

  public void collectChildrenData(Node parentNode) {
    final Set<Node> children = parentNode.getChildren();
    final AtomicInteger allChildrenNumber = new AtomicInteger(children.size());
    // We calculate the sum of the prices of all children to find
    // the average price (it must be set as the price of the
    // parent node).
    final int childrenPriceSum = children.stream()
      .mapToInt(n -> {
        if (n.getType().equals(Type.CATEGORY)) {
          // If the child node is a category, you need to collect data
          // for it in the same way as for the parent node.
          collectChildrenData(n);
          // Below is a very important line. We do not increment
          // allChildrenNumber, but add to it the number of
          // children of the current child (which is the category) - 1
          // (subtract the category, since it is not counted). This
          // is necessary so that the parent node, when calculating
          // the average price, takes into account not only the number
          // of its children, but also the total number of children
          // (own children + children of subcategories + children of
          // subcategories of subcategories, etc).
          allChildrenNumber.addAndGet(n.getChildren().size() - 1);
          // We don't need the price of this category (because its
          // price is the average price of its children), but the
          // sum of the prices of its children.
          return n.getChildrenPriceSum();
        }
        return n.getPrice();
      })
      .reduce(0, Integer::sum);

    Integer averageChildrenPrice = childrenPriceSum / Math.max(1, allChildrenNumber.get());
    // If the number of all children is zero, then according to the
    // agreement with OpenAPI, we must set the value to NULL.
    if (allChildrenNumber.get() == 0) {
      averageChildrenPrice = null;
    }

    parentNode.setPrice(averageChildrenPrice);
    parentNode.setChildrenPriceSum(childrenPriceSum);
  }
}
