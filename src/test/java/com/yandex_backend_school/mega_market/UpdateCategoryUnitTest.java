package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.entity.NodeChange;
import com.yandex_backend_school.mega_market.repository.NodeChangeRepository;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
import java.util.UUID;
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
 * @created 22/06/2022 - 12:22 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateCategoryUnitTest {
  @SpyBean
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @MockBean
  private NodeChangeRepository nodeChangeRepository;

  @Test
  public void shouldDoNothingButJustTerminateMethodBecauseNodeIsNull() {
    final Node node = Mockito.spy(new Node());

    final Node parentNode = node.getParentNode();

    final LocalDateTime updateDate = LocalDateTime.now();

    nodeService.updateCategory(null, updateDate);

    Mockito.verify(node, Mockito.times(0))
      .setDate(ArgumentMatchers.eq(updateDate));

    Mockito.verify(nodeRepository, Mockito.times(0))
      .save(ArgumentMatchers.eq(node));

    Mockito.verify(node, Mockito.times(0))
      .getId();

    Mockito.verify(nodeRepository, Mockito.times(0))
      .updatePriceBasedOnChildren(ArgumentMatchers.eq(node.getId()));

    Mockito.verify(nodeRepository, Mockito.times(0))
      .flush();

    Mockito.verify(nodeChangeRepository, Mockito.times(0))
      .saveAndFlush(ArgumentMatchers.any(NodeChange.class));

    Mockito.verify(nodeService, Mockito.times(1))
      .updateCategory(
        ArgumentMatchers.eq(parentNode),
        ArgumentMatchers.eq(updateDate));
  }

  @Test
  public void shouldSaveNodeAndUpdateItsPriceAndSaveNodeChangeAndRecallUpdateCategoryMethodForNodeParent() {
    final Node node = Mockito.spy(new Node());
    node.setId(UUID.randomUUID().toString());

    final Node parentNode = node.getParentNode();

    final LocalDateTime updateDate = LocalDateTime.now();

    Mockito.doNothing()
      .when(nodeService)
      .updateCategory(
        ArgumentMatchers.eq(parentNode),
        ArgumentMatchers.eq(updateDate));

    nodeService.updateCategory(node, updateDate);

    Mockito.verify(node, Mockito.times(1))
      .setDate(ArgumentMatchers.eq(updateDate));

    Mockito.verify(nodeRepository, Mockito.times(1))
      .save(ArgumentMatchers.eq(node));

    Mockito.verify(node, Mockito.times(1))
      .getId();

    Mockito.verify(nodeRepository, Mockito.times(1))
      .updatePriceBasedOnChildren(ArgumentMatchers.eq(node.getId()));

    Mockito.verify(nodeRepository, Mockito.times(1))
      .flush();

    Mockito.verify(nodeChangeRepository, Mockito.times(1))
      .saveAndFlush(ArgumentMatchers.any(NodeChange.class));

    Mockito.verify(nodeService, Mockito.times(1))
      .updateCategory(
        ArgumentMatchers.eq(parentNode),
        ArgumentMatchers.eq(updateDate));
  }
}
