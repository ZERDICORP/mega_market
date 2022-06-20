package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.entity.NodeChange;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.NodeChangeRepository;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
 * @created 18/06/2022 - 6:39 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImportNodesUnitTest {
  @Autowired
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @MockBean
  private NodeChangeRepository nodeChangeRepository;

  @Test
  public void shouldSaveNodeThenSaveNodeChangeBecauseNodeTypeIsOffer() {
    final ImportNodesRequestBodyItem requestBodyItem = Mockito.spy(new ImportNodesRequestBodyItem());
    requestBodyItem.setType(Type.OFFER);

    final List<ImportNodesRequestBodyItem> items = new ArrayList<>();
    items.add(requestBodyItem);

    final ImportNodesRequestBody requestBody = Mockito.spy(new ImportNodesRequestBody());
    requestBody.setItems(items);
    requestBody.setUpdateDate(LocalDateTime.now());

    final Node parentNode = Mockito.spy(new Node());

    Mockito.when(nodeRepository.findById(ArgumentMatchers.eq(requestBodyItem.getParentId())))
      .thenReturn(Optional.of(parentNode));

    nodeService.importNodes(requestBody);

    Mockito.verify(requestBody, Mockito.times(1))
      .getUpdateDate();

    Mockito.verify(requestBody, Mockito.times(1))
      .getItems();

    Mockito.verify(requestBodyItem, Mockito.times(2))
      .getId();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getName();

    Mockito.verify(requestBodyItem, Mockito.times(3))
      .getParentId();

    Mockito.verify(requestBodyItem, Mockito.times(2))
      .getPrice();

    Mockito.verify(requestBodyItem, Mockito.times(2))
      .getType();

    Mockito.verify(nodeRepository, Mockito.times(1))
      .save(ArgumentMatchers.any(Node.class));

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findById(ArgumentMatchers.eq(requestBodyItem.getParentId()));

    Mockito.verify(nodeChangeRepository, Mockito.times(2))
      .save(ArgumentMatchers.any(NodeChange.class));
  }

  @Test
  public void shouldSaveNodeWithoutSavingNodeChangeBecauseNodeTypeIsCategory() {
    final ImportNodesRequestBodyItem requestBodyItem = Mockito.spy(new ImportNodesRequestBodyItem());
    requestBodyItem.setType(Type.CATEGORY);

    final List<ImportNodesRequestBodyItem> items = new ArrayList<>();
    items.add(requestBodyItem);

    final ImportNodesRequestBody requestBody = Mockito.spy(new ImportNodesRequestBody());
    requestBody.setItems(items);
    requestBody.setUpdateDate(LocalDateTime.now());

    final Node parentNode = Mockito.spy(new Node());

    Mockito.when(nodeRepository.findById(ArgumentMatchers.eq(requestBodyItem.getParentId())))
      .thenReturn(Optional.of(parentNode));

    nodeService.importNodes(requestBody);

    Mockito.verify(requestBody, Mockito.times(1))
      .getUpdateDate();

    Mockito.verify(requestBody, Mockito.times(1))
      .getItems();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getId();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getName();

    Mockito.verify(requestBodyItem, Mockito.times(2))
      .getParentId();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getPrice();

    Mockito.verify(requestBodyItem, Mockito.times(2))
      .getType();

    Mockito.verify(nodeRepository, Mockito.times(1))
      .save(ArgumentMatchers.any(Node.class));

    Mockito.verify(nodeRepository, Mockito.times(0))
      .findById(ArgumentMatchers.eq(requestBodyItem.getParentId()));

    Mockito.verify(nodeChangeRepository, Mockito.times(0))
      .save(ArgumentMatchers.any(NodeChange.class));
  }
}
