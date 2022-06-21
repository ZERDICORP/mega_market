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
  public NodeService(NodeRepository nodeRepository, NodeChangeRepository nodeChangeRepository,
                     EntityManager entityManager) {
    this.nodeRepository = nodeRepository;
    this.nodeChangeRepository = nodeChangeRepository;
    this.entityManager = entityManager;
  }

  public GetNodesResponseBody getNodeChangeStatistics(String id, LocalDateTime dateStart, LocalDateTime dateEnd) {
    final Node node = nodeRepository.findById(id)
      .orElseThrow(ItemNotFoundException::new);

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeTemplate.DATABASE_DATE_FORMAT);
    dateStart = Optional.ofNullable(dateStart)
      .orElse(LocalDateTime.parse(DateTimeTemplate.MIN, formatter));
    dateEnd = Optional.ofNullable(dateEnd)
      .orElse(LocalDateTime.parse(DateTimeTemplate.MAX, formatter));

    final List<GetNodesResponseBodyItem> items = nodeChangeRepository.findByNodeIdInDateRange(id,
        dateStart, dateEnd).stream()
      .map(nc -> new GetNodesResponseBodyItem(node.getId(), node.getName(), node.getType(), node.getParentId(),
        nc.getDate(), nc.getPrice()))
      .toList();

    return new GetNodesResponseBody(items);
  }

  public GetNodesResponseBody getUpdatedIn24HoursOffers(LocalDateTime date) {
    final List<GetNodesResponseBodyItem> items = nodeRepository.findUpdatedIn24Hours(Type.OFFER, date)
      .stream()
      .map(n -> new GetNodesResponseBodyItem(n.getId(), n.getName(), n.getType(), n.getParentId(), n.getDate(),
        n.getPrice()))
      .toList();

    return new GetNodesResponseBody(items);
  }

  @Transactional
  public void deleteNode(String id) {
    final Node node = nodeRepository.findById(id)
      .orElseThrow(ItemNotFoundException::new);

    nodeRepository.delete(node);
    nodeRepository.flush();

    updateParentNode(node.getParentNode(), LocalDateTime.now());
  }

  public Node getNode(String parentId) {
    return nodeRepository.findById(parentId)
      .orElseThrow(ItemNotFoundException::new);
  }

  @Transactional
  public void importNodes(ImportNodesRequestBody requestBody) {
    final LocalDateTime updateDate = requestBody.getUpdateDate();
    final List<ImportNodesRequestBodyItem> items = requestBody.getItems();

    // Sorting items so that categories come first, then offers.
    items.sort(Comparator.comparingInt(n -> n.getType().ordinal()));
    items.forEach(i -> saveNode(i, updateDate));
  }

  public void saveNode(ImportNodesRequestBodyItem item, LocalDateTime updateDate) {
    Node parentNode = null;
    if (item.getParentId() != null) {
      entityManager.clear();
      parentNode = nodeRepository.findById(item.getParentId())
        .orElse(null);
    }

    final Node node = new Node(item.getId(), item.getName(), item.getPrice(), item.getType(),
      updateDate, parentNode);

    nodeRepository.saveAndFlush(node);

    if (item.getType().equals(Type.OFFER)) {
      // We save the state of the node (price, date) for the
      // subsequent collection of statistics.
      nodeChangeRepository.saveAndFlush(new NodeChange(node, updateDate, item.getPrice()));
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
    nodeRepository.updatePriceBasedOnChildren(parentNode.getId());
    nodeRepository.flush();

    nodeChangeRepository.saveAndFlush(new NodeChange(parentNode, updateDate, parentNode.getPrice()));

    updateParentNode(parentNode.getParentNode(), updateDate);
  }
}
