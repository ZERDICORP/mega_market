package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.pojo.GetNodeResponseBodyItem;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 19/06/2022 - 12:18 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetNodeUnitTest {
  @SpyBean
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @Test
  public void shouldFindNodesByParentAndDoSomeCalculations() {
    final Node node = new Node();
    node.setPrice(100);
    node.setDate(LocalDateTime.now());
    node.setType(Type.OFFER);

    final List<Node> nodes = new ArrayList<>();
    nodes.add(node);

    final Node parentNode = Mockito.spy(new Node());
    parentNode.setId(UUID.randomUUID().toString());
    parentNode.setType(Type.OFFER);

    Mockito.when(nodeRepository.findByParentId(parentNode.getId()))
      .thenReturn(nodes);

    final GetNodeResponseBodyItem responseBody = nodeService.getNode(parentNode);
    assertEquals(parentNode.getId(), responseBody.getId());
    assertEquals(node.getPrice(), responseBody.getPrice());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findByParentId(ArgumentMatchers.eq(parentNode.getId()));

    Mockito.verify(parentNode, Mockito.times(1))
      .getType();

    Mockito.verify(parentNode, Mockito.times(5))
      .getId();

    Mockito.verify(parentNode, Mockito.times(1))
      .getName();

    Mockito.verify(parentNode, Mockito.times(1))
      .getParentId();

    Mockito.verify(parentNode, Mockito.times(1))
      .getDate();

    Mockito.verify(parentNode, Mockito.times(0))
      .getPrice();

    Mockito.verify(nodeService, Mockito.times(1))
      .getNode(ArgumentMatchers.eq(parentNode));
  }

  @Test
  public void shouldThrowExceptionBecauseFindByIdMethodReturnsNull() {
    assertThrows(ItemNotFoundException.class, () -> {
      final Node parentNode = Mockito.spy(new Node());
      parentNode.setId(UUID.randomUUID().toString());
      parentNode.setType(Type.OFFER);

      Mockito.when(nodeRepository.findById(parentNode.getId()))
        .thenReturn(Optional.empty());

      nodeService.getNode(parentNode.getId());
    });
  }

  @Test
  public void shouldFindNodeAndDoNotCallGetNodeTreeMethodBecauseNodeIsOffer() {
    final Node parentNode = Mockito.spy(new Node());
    parentNode.setId(UUID.randomUUID().toString());
    parentNode.setType(Type.OFFER);

    Mockito.when(nodeRepository.findById(parentNode.getId()))
      .thenReturn(Optional.of(parentNode));

    final GetNodeResponseBodyItem getNodesResponseBody = nodeService.getNode(parentNode.getId());
    assertNotNull(getNodesResponseBody);
    assertEquals(getNodesResponseBody.getId(), parentNode.getId());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(parentNode.getId()));

    Mockito.verify(parentNode, Mockito.times(2))
      .getType();

    Mockito.verify(parentNode, Mockito.times(5))
      .getId();

    Mockito.verify(parentNode, Mockito.times(1))
      .getName();

    Mockito.verify(parentNode, Mockito.times(1))
      .getParentId();

    Mockito.verify(parentNode, Mockito.times(1))
      .getDate();

    Mockito.verify(parentNode, Mockito.times(1))
      .getPrice();

    Mockito.verify(nodeService, Mockito.times(0))
      .getNode(ArgumentMatchers.eq(parentNode));
  }

  @Test
  public void shouldFindNodeAndCallGetNodeTreeMethodBecauseNodeIsCategory() {
    final Node parentNode = Mockito.spy(new Node());
    parentNode.setId(UUID.randomUUID().toString());
    parentNode.setType(Type.CATEGORY);

    Mockito.when(nodeRepository.findById(parentNode.getId()))
      .thenReturn(Optional.of(parentNode));

    Mockito.doReturn(null)
      .when(nodeService)
      .getNode(ArgumentMatchers.eq(parentNode));

    final GetNodeResponseBodyItem getNodesResponseBody = nodeService.getNode(parentNode.getId());
    assertNull(getNodesResponseBody);

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(parentNode.getId()));

    Mockito.verify(parentNode, Mockito.times(1))
      .getType();

    Mockito.verify(parentNode, Mockito.times(3))
      .getId();

    Mockito.verify(parentNode, Mockito.times(0))
      .getName();

    Mockito.verify(parentNode, Mockito.times(0))
      .getParentId();

    Mockito.verify(parentNode, Mockito.times(0))
      .getDate();

    Mockito.verify(parentNode, Mockito.times(0))
      .getPrice();

    Mockito.verify(nodeService, Mockito.times(1))
      .getNode(ArgumentMatchers.eq(parentNode));
  }
}
