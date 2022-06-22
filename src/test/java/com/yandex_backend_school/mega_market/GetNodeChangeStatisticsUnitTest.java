package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.entity.NodeChange;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.repository.NodeChangeRepository;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 22/06/2022 - 12:55 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetNodeChangeStatisticsUnitTest {
  @Autowired
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @MockBean
  private NodeChangeRepository nodeChangeRepository;

  @Test
  public void shouldThrowExceptionBecauseFindByIdMethodReturnsNull() {
    assertThrows(ItemNotFoundException.class, () -> {
      final Node node = Mockito.spy(new Node());
      node.setId(UUID.randomUUID().toString());
      node.setType(Type.CATEGORY);

      Mockito.when(nodeRepository.findById(node.getId()))
        .thenReturn(Optional.empty());

      nodeService.deleteNode(node.getId());
    });
  }

  @Test
  public void shouldFindNodeAndThenFindNodeChangesButDateStartAndDateEndShouldBeReplacedWithMinAndMaxDates() {
    final String nodeId = UUID.randomUUID().toString();

    final Node node = Mockito.spy(new Node());
    node.setId(nodeId);
    node.setType(Type.CATEGORY);

    Mockito.doReturn(Optional.of(node))
      .when(nodeRepository)
      .findById(ArgumentMatchers.eq(nodeId));

    final NodeChange nodeChange = Mockito.spy(new NodeChange());
    nodeChange.setNode(node);

    final List<NodeChange> foundNodeChanges = new ArrayList<>();
    foundNodeChanges.add(nodeChange);

    Mockito.doReturn(foundNodeChanges)
      .when(nodeChangeRepository)
      .findByNodeIdInDateRange(
        ArgumentMatchers.eq(nodeId),
        ArgumentMatchers.any(LocalDateTime.class),
        ArgumentMatchers.any(LocalDateTime.class));

    final GetNodesResponseBody responseBody = nodeService.getNodeChangeStatistics(nodeId, null, null);
    assertNotNull(responseBody);
    assertNotNull(responseBody.getItems());
    assertEquals(1, responseBody.getItems().size());
    assertEquals(nodeId, responseBody.getItems().get(0).getId());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(nodeId));

    Mockito.doReturn(foundNodeChanges)
      .when(nodeChangeRepository)
      .findByNodeIdInDateRange(
        ArgumentMatchers.eq(nodeId),
        ArgumentMatchers.any(LocalDateTime.class),
        ArgumentMatchers.any(LocalDateTime.class));

    Mockito.verify(node, Mockito.times(1))
      .getId();

    Mockito.verify(node, Mockito.times(1))
      .getName();

    Mockito.verify(node, Mockito.times(1))
      .getType();

    Mockito.verify(node, Mockito.times(1))
      .getParentId();

    Mockito.verify(nodeChange, Mockito.times(1))
      .getDate();

    Mockito.verify(nodeChange, Mockito.times(1))
      .getPrice();
  }

  @Test
  public void shouldFindNodeAndThenFindNodeChangesAndReturnGetNodesResponseBody() {
    final LocalDateTime dateStart = LocalDateTime.MIN;
    final LocalDateTime dateEnd = LocalDateTime.MAX;

    final String nodeId = UUID.randomUUID().toString();

    final Node node = Mockito.spy(new Node());
    node.setId(nodeId);
    node.setType(Type.CATEGORY);

    Mockito.doReturn(Optional.of(node))
      .when(nodeRepository)
      .findById(ArgumentMatchers.eq(nodeId));

    final NodeChange nodeChange = Mockito.spy(new NodeChange());
    nodeChange.setNode(node);

    final List<NodeChange> foundNodeChanges = new ArrayList<>();
    foundNodeChanges.add(nodeChange);

    Mockito.doReturn(foundNodeChanges)
      .when(nodeChangeRepository)
      .findByNodeIdInDateRange(
        ArgumentMatchers.eq(nodeId),
        ArgumentMatchers.eq(dateStart),
        ArgumentMatchers.eq(dateEnd));

    final GetNodesResponseBody responseBody = nodeService.getNodeChangeStatistics(nodeId, dateStart, dateEnd);
    assertNotNull(responseBody);
    assertNotNull(responseBody.getItems());
    assertEquals(1, responseBody.getItems().size());
    assertEquals(nodeId, responseBody.getItems().get(0).getId());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(nodeId));

    Mockito.verify(nodeChangeRepository, Mockito.times(1))
      .findByNodeIdInDateRange(
        ArgumentMatchers.eq(nodeId),
        ArgumentMatchers.eq(dateStart),
        ArgumentMatchers.eq(dateEnd));

    Mockito.verify(node, Mockito.times(1))
      .getId();

    Mockito.verify(node, Mockito.times(1))
      .getName();

    Mockito.verify(node, Mockito.times(1))
      .getType();

    Mockito.verify(node, Mockito.times(1))
      .getParentId();

    Mockito.verify(nodeChange, Mockito.times(1))
      .getDate();

    Mockito.verify(nodeChange, Mockito.times(1))
      .getPrice();
  }
}
