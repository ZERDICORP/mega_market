package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.exception.ItemNotFoundException;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.repository.NodeChangeRepository;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
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
 * @created 20/06/2022 - 5:30 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetNodeStatisticsUnitTest {
  @Autowired
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @MockBean
  private NodeChangeRepository nodeChangeRepository;

  @Test
  public void shouldThrowExceptionBecauseFindByIdMethodReturnsNull() {
    assertThrows(ItemNotFoundException.class, () -> {
      final String id = UUID.randomUUID().toString();

      Mockito.doReturn(Optional.empty())
        .when(nodeRepository)
        .findById(ArgumentMatchers.eq(id));

      Mockito.doReturn(new ArrayList<>())
        .when(nodeChangeRepository)
        .findByNodeIdInDateRange(
          ArgumentMatchers.eq(id),
          ArgumentMatchers.eq(null),
          ArgumentMatchers.eq(null));

      nodeService.getNodeChangeStatistics(id, null, null);
    });
  }

  @Test
  public void shouldFindOffersAndReturnGetSalesResponseBody() {
    final String id = UUID.randomUUID().toString();
    final LocalDateTime dateStart = LocalDateTime.now();
    final LocalDateTime dateEnd = LocalDateTime.now();

    final Node node = Mockito.spy(new Node());

    Mockito.doReturn(Optional.of(node))
      .when(nodeRepository)
      .findById(ArgumentMatchers.eq(id));

    Mockito.doReturn(new ArrayList<>())
      .when(nodeChangeRepository)
      .findByNodeIdInDateRange(
        ArgumentMatchers.eq(id),
        ArgumentMatchers.eq(dateStart),
        ArgumentMatchers.eq(dateEnd));

    final GetNodesResponseBody getNodesResponseBody = nodeService.getNodeChangeStatistics(id, dateStart, dateEnd);
    assertNotNull(getNodesResponseBody);
    assertNotNull(getNodesResponseBody.getItems());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(id));

    Mockito.verify(nodeChangeRepository, Mockito.times(1))
      .findByNodeIdInDateRange(
        ArgumentMatchers.eq(id),
        ArgumentMatchers.eq(dateStart),
        ArgumentMatchers.eq(dateEnd));
  }
}
