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
        node.getParentNode() == null ? null : node.getParentNode().getId(),
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

    parentNode.setDate(updateDate);
    nodeRepository.save(parentNode);
    nodeRepository.countChildrenAveragePrice(parentNode.getId());
    nodeRepository.flush();

    nodeChangeRepository.saveAndFlush(new NodeChange(
      updateDate,
      parentNode,
      Optional.ofNullable(parentNode.getPrice()).orElse(0)));

    if (parentNode.getParentNode() != null) {
      updateParentNode(parentNode.getParentNode(), updateDate);
    }
  }
}
