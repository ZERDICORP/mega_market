package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
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
 * @created 20/06/2022 - 11:00 AM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteNodeUnitTest {
  @SpyBean
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @Test
  public void shouldDeleteByParentIdAndThenDeleteParentNode() {
    final Node parentNode = Mockito.spy(new Node());
    parentNode.setId(UUID.randomUUID().toString());

    Mockito.when(nodeRepository.deleteByParentId(parentNode.getId()))
      .thenReturn(new ArrayList<>());

    nodeService.deleteNodeTree(parentNode);

    Mockito.verify(nodeRepository, Mockito.times(1))
      .deleteByParentId(ArgumentMatchers.eq(parentNode.getId()));

    Mockito.verify(nodeRepository, Mockito.times(1))
      .delete(ArgumentMatchers.eq(parentNode));
  }

  @Test
  public void shouldThrowExceptionBecauseFindByIdMethodReturnsNull() {
    assertThrows(ItemNotFoundException.class, () -> {
      final Node parentNode = Mockito.spy(new Node());
      parentNode.setId(UUID.randomUUID().toString());
      parentNode.setType(Type.OFFER);

      Mockito.when(nodeRepository.findById(parentNode.getId()))
        .thenReturn(Optional.empty());

      nodeService.deleteNodeTree(parentNode.getId());
    });
  }

  @Test
  public void shouldFindNodeAndReturnItBecauseNodeIsOffer() {
    final Node parentNode = Mockito.spy(new Node());
    parentNode.setId(UUID.randomUUID().toString());
    parentNode.setType(Type.OFFER);

    Mockito.when(nodeRepository.findById(parentNode.getId()))
      .thenReturn(Optional.of(parentNode));

    nodeService.deleteNodeTree(parentNode.getId());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(parentNode.getId()));

    Mockito.verify(nodeRepository, Mockito.times(1))
      .delete(ArgumentMatchers.eq(parentNode));

    Mockito.verify(parentNode, Mockito.times(1))
      .getType();

    Mockito.verify(nodeService, Mockito.times(0))
      .deleteNodeTree(ArgumentMatchers.eq(parentNode));

    Mockito.verify(nodeService, Mockito.times(1))
      .deleteNodeTree(ArgumentMatchers.eq(parentNode.getId()));
  }

  @Test
  public void shouldFindNodeAndCallDeleteNodeTreeMethodBecauseNodeIsCategory() {
    final Node parentNode = Mockito.spy(new Node());
    parentNode.setId(UUID.randomUUID().toString());
    parentNode.setType(Type.CATEGORY);

    Mockito.when(nodeRepository.findById(parentNode.getId()))
      .thenReturn(Optional.of(parentNode));

    Mockito.doNothing()
      .when(nodeService)
      .deleteNodeTree(ArgumentMatchers.eq(parentNode));

    nodeService.deleteNodeTree(parentNode.getId());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(parentNode.getId()));

    Mockito.verify(nodeRepository, Mockito.times(0))
      .delete(ArgumentMatchers.eq(parentNode));

    Mockito.verify(parentNode, Mockito.times(1))
      .getType();

    Mockito.verify(nodeService, Mockito.times(1))
      .deleteNodeTree(ArgumentMatchers.eq(parentNode));

    Mockito.verify(nodeService, Mockito.times(1))
      .deleteNodeTree(ArgumentMatchers.eq(parentNode.getId()));
  }
}
