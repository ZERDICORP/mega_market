package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
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
  public void shouldFindNodeAndThenDeleteItAndFlushRepositoryAndUpdateNodeParent() {
    final Node node = Mockito.spy(new Node());
    node.setId(UUID.randomUUID().toString());
    node.setType(Type.CATEGORY);

    Mockito.when(nodeRepository.findById(node.getId()))
      .thenReturn(Optional.of(node));

    Mockito.doNothing()
      .when(nodeService)
      .updateCategory(
        ArgumentMatchers.eq(node),
        ArgumentMatchers.any(LocalDateTime.class));

    nodeService.deleteNode(node.getId());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(node.getId()));

    Mockito.verify(nodeRepository, Mockito.times(1))
      .delete(ArgumentMatchers.eq(node));

    Mockito.verify(nodeRepository, Mockito.times(1))
      .flush();

    Mockito.verify(nodeService, Mockito.times(1))
      .updateCategory(
        ArgumentMatchers.eq(node.getParentNode()),
        ArgumentMatchers.any(LocalDateTime.class));
  }
}
