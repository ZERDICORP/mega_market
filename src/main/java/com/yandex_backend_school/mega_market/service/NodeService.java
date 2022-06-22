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

  @Autowired
  public NodeService(NodeRepository nodeRepository, NodeChangeRepository nodeChangeRepository) {
    this.nodeRepository = nodeRepository;
    this.nodeChangeRepository = nodeChangeRepository;
  }

  public GetNodesResponseBody getNodeChangeStatistics(String id, LocalDateTime dateStart, LocalDateTime dateEnd) {
    final Node node = nodeRepository.findById(id)
      .orElseThrow(ItemNotFoundException::new);

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeTemplate.DATABASE_DATE_FORMAT);
    // If dateStart is null, replace it with the minimum date
    // value (thus searching from the lower bound).
    dateStart = Optional.ofNullable(dateStart)
      .orElse(LocalDateTime.parse(DateTimeTemplate.MIN, formatter));
    // If dateEnd is null, replace it with the maximum date
    // value (thus searching from the upper bound).
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

    // After deleting any node, you need to update the state
    // of the parent (price, last update date).
    updateCategory(node.getParentNode(), LocalDateTime.now());
  }

  public Node getNode(String id) {
    return nodeRepository.findById(id)
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
      parentNode = nodeRepository.findById(item.getParentId())
        .orElse(null);
    }

    final Node node = new Node(item.getId(), item.getName(), item.getPrice(), item.getType(),
      updateDate, parentNode);

    nodeRepository.saveAndFlush(node);

    if (item.getType().equals(Type.OFFER)) {
      // We save the state of the node (price, date) for statistics.
      nodeChangeRepository.saveAndFlush(new NodeChange(node, updateDate, item.getPrice()));
      updateCategory(parentNode, updateDate);
      return;
    }

    updateCategory(node, updateDate);
  }

  public void updateCategory(Node node, LocalDateTime updateDate) {
    if (node == null) {
      return;
    }

    node.setDate(updateDate);
    nodeRepository.save(node);
    nodeRepository.updatePriceBasedOnChildren(node.getId());
    nodeRepository.flush();

    nodeChangeRepository.saveAndFlush(new NodeChange(node, updateDate, node.getPrice()));

    // Since the category structure is cascading in nature (in our
    // case, the price of each category is the average of the
    // prices of all child offers), updating one of them, you need
    // to recursively go up and update each subsequent parent until
    // we hit null.
    //
    // By the way, this is somewhat similar to the wave effect, but
    // in one-dimensional space and with the restriction that you
    // can only move in one direction (we move up at the expense of
    // parentNode).
    updateCategory(node.getParentNode(), updateDate);
  }
}
