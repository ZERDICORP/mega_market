package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.entity.NodeChange;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBodyItem;
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
 * @created 22/06/2022 - 11:45 AM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SaveNodeUnitTest {
  @SpyBean
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @MockBean
  private NodeChangeRepository nodeChangeRepository;

  @Test
  public void shouldSaveNodeAndThenSaveNodeChangeAndUpdateParentNode() {
    final ImportNodesRequestBodyItem item = Mockito.spy(new ImportNodesRequestBodyItem());
    item.setType(Type.OFFER);
    item.setId(UUID.randomUUID().toString());

    final LocalDateTime updateDate = LocalDateTime.now();

    Mockito.doNothing()
      .when(nodeService)
      .updateCategory(
        ArgumentMatchers.nullable(Node.class),
        ArgumentMatchers.eq(updateDate));

    nodeService.saveNode(item, updateDate);

    Mockito.verify(item, Mockito.times(1))
      .getParentId();

    Mockito.verify(item, Mockito.times(1))
      .getId();

    Mockito.verify(item, Mockito.times(1))
      .getName();

    Mockito.verify(item, Mockito.times(2))
      .getPrice();

    Mockito.verify(item, Mockito.times(2))
      .getType();

    Mockito.verify(nodeRepository, Mockito.times(1))
      .saveAndFlush(ArgumentMatchers.any(Node.class));

    Mockito.verify(nodeChangeRepository, Mockito.times(1))
      .saveAndFlush(ArgumentMatchers.any(NodeChange.class));

    Mockito.verify(nodeService, Mockito.times(1))
      .updateCategory(
        ArgumentMatchers.nullable(Node.class),
        ArgumentMatchers.eq(updateDate));
  }

  @Test
  public void shouldSaveNodeAndThenUpdateItAsCategoryBecauseNodeTypeIsCategoryButParentIdNotNull() {
    final String itemId = UUID.randomUUID().toString();
    final String prentId = UUID.randomUUID().toString();

    final ImportNodesRequestBodyItem item = Mockito.spy(new ImportNodesRequestBodyItem());
    item.setType(Type.CATEGORY);
    item.setId(itemId);
    item.setParentId(prentId);

    final LocalDateTime updateDate = LocalDateTime.now();

    Mockito.doNothing()
      .when(nodeService)
      .updateCategory(
        ArgumentMatchers.any(Node.class),
        ArgumentMatchers.eq(updateDate));

    nodeService.saveNode(item, updateDate);

    Mockito.verify(item, Mockito.times(2))
      .getParentId();

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(item.getParentId()));

    Mockito.verify(item, Mockito.times(1))
      .getId();

    Mockito.verify(item, Mockito.times(1))
      .getName();

    Mockito.verify(item, Mockito.times(1))
      .getPrice();

    Mockito.verify(item, Mockito.times(2))
      .getType();

    Mockito.verify(nodeRepository, Mockito.times(1))
      .saveAndFlush(ArgumentMatchers.any(Node.class));

    Mockito.verify(nodeChangeRepository, Mockito.times(0))
      .saveAndFlush(ArgumentMatchers.any(NodeChange.class));

    Mockito.verify(nodeService, Mockito.times(1))
      .updateCategory(
        ArgumentMatchers.any(Node.class),
        ArgumentMatchers.eq(updateDate));
  }

  @Test
  public void shouldSaveNodeAndThenUpdateItAsCategoryBecauseNodeTypeIsCategory() {
    final String itemId = UUID.randomUUID().toString();

    final ImportNodesRequestBodyItem item = Mockito.spy(new ImportNodesRequestBodyItem());
    item.setType(Type.CATEGORY);
    item.setId(itemId);

    final LocalDateTime updateDate = LocalDateTime.now();

    Mockito.doNothing()
      .when(nodeService)
      .updateCategory(
        ArgumentMatchers.any(Node.class),
        ArgumentMatchers.eq(updateDate));

    nodeService.saveNode(item, updateDate);

    Mockito.verify(item, Mockito.times(1))
      .getParentId();

    Mockito.verify(nodeRepository, Mockito.times(0))
      .findById(ArgumentMatchers.eq(item.getParentId()));

    Mockito.verify(item, Mockito.times(1))
      .getId();

    Mockito.verify(item, Mockito.times(1))
      .getName();

    Mockito.verify(item, Mockito.times(1))
      .getPrice();

    Mockito.verify(item, Mockito.times(2))
      .getType();

    Mockito.verify(nodeRepository, Mockito.times(1))
      .saveAndFlush(ArgumentMatchers.any(Node.class));

    Mockito.verify(nodeChangeRepository, Mockito.times(0))
      .saveAndFlush(ArgumentMatchers.any(NodeChange.class));

    Mockito.verify(nodeService, Mockito.times(1))
      .updateCategory(
        ArgumentMatchers.any(Node.class),
        ArgumentMatchers.eq(updateDate));
  }
}
