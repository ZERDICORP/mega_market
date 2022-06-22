package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
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
 * @created 19/06/2022 - 12:18 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetNodeUnitTest {
  @Autowired
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @Test
  public void shouldThrowExceptionBecauseFindByIdMethodReturnsNull() {
    assertThrows(ItemNotFoundException.class, () -> {
      final String id = UUID.randomUUID().toString();

      Mockito.doReturn(Optional.empty())
        .when(nodeRepository)
        .findById(ArgumentMatchers.eq(id));

      nodeService.getNode(id);
    });
  }

  @Test
  public void shouldFindNodeByIdAndReturnIt() {
    final String id = UUID.randomUUID().toString();

    final Node node = new Node();

    Mockito.doReturn(Optional.of(node))
      .when(nodeRepository)
      .findById(ArgumentMatchers.eq(id));

    final Node foundNode = nodeService.getNode(id);
    assertEquals(node, foundNode);

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(id));
  }
}
