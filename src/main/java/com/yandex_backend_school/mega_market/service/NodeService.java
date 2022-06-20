package com.yandex_backend_school.mega_market.service;

import com.yandex_backend_school.mega_market.constant.DateTimeTemplate;
import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.entity.NodeChange;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.pojo.GetNodeResponseBody;
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
        // The construct on the line below is just a replacement for
        // the following construct:
        // dateStart == null ? LocalDateTime.parse(DateTimeTemplate.MIN, formatter) : dateStart;
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
    // We collect all the id in the list so that later we can
    // delete the node changes in the batch using them.
    final List<String> deletedNodeIds = Stream.concat(deletedNodes.stream(), Stream.of(parentNode))
      .map(Node::getId)
      .toList();

    // For each child node, if it is a category, we must do
    // the same as for the current parent node.
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

    // If the node is an offer, then by definition it has no
    // children, so just delete it.
    if (parentNode.getType().equals(Type.OFFER)) {
      nodeRepository.delete(parentNode);
      return;
    }

    // Otherwise, if the node is a category, we need to delete all
    // of its children, as well as the children of the children (if
    // the latter are categories).
    deleteNode(parentNode);
  }

  public GetNodeResponseBody getNode(Node parentNode) {
    final List<GetNodeResponseBody> nodes = nodeRepository.findByParentId(parentNode.getId())
      .stream()
      .map(n -> {
        // If the child node is an offer, simply return it.
        if (n.getType().equals(Type.OFFER)) {
          return new GetNodeResponseBody(
            n.getId(),
            n.getName(),
            n.getType(),
            n.getParentId(),
            n.getDate(),
            n.getPrice(),
            null);
        }

        // If the child is a category, we need to find its children.
        return getNode(n);
      })
      .toList();

    // We find the child with the maximum date of the last update
    // and set this date to the parent node, since the update of
    // the child is also the update of the parent node.
    nodes.stream()
      .map(GetNodeResponseBody::getDate)
      .max(LocalDateTime::compareTo)
      .ifPresent(parentNode::setDate);

    final AtomicInteger allChildrenNumber = new AtomicInteger(nodes.size());

    // We calculate the sum of the prices of all children to find
    // the average price (it must be set as the price of the
    // parent node).
    final int priceSum = nodes.stream()
      .mapToInt(n -> {
        if (n.getType().equals(Type.CATEGORY)) {
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

    Integer averagePrice = priceSum / Math.max(1, allChildrenNumber.get());
    // If the number of all children is zero, then according to the
    // agreement with OpenAPI, we must set the value to NULL.
    if (allChildrenNumber.get() == 0) {
      averagePrice = null;
    }

    return new GetNodeResponseBody(
      parentNode.getId(),
      parentNode.getName(),
      parentNode.getType(),
      parentNode.getParentId(),
      parentNode.getDate(),
      averagePrice,
      nodes,
      priceSum);
  }

  public GetNodeResponseBody getNode(String parentId) {
    final Node parentNode = nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);

    // If the node is an offer, then we do not need to look for
    // its children, so we simply return it.
    if (parentNode.getType().equals(Type.OFFER)) {
      return new GetNodeResponseBody(
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
        // We save the state of the node (price, date) for the
        // subsequent collection of statistics.
        nodeChangeRepository.save(new NodeChange(
          n.getId(),
          updateDate,
          n.getPrice()));

        // Since we have changed/created a node, we must find the
        // parent node and save the new state for it.
        nodeRepository.findById(n.getParentId())
          .ifPresent(pn -> nodeChangeRepository.save(new NodeChange(
            pn.getId(),
            updateDate,
            // Since we are calculating the state of the category,
            // we must recalculate its price (remember, the price of
            // a category is the average price of all its children),
            // which means we need to call the getNode method, which
            // recursively calculates the price for the categories.
            getNode(pn).getPrice())));
      }
    });
  }
}
