package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import com.yandex_backend_school.mega_market.util.SpiedArrayList;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
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
 * @created 18/06/2022 - 6:39 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImportNodesUnitTest {
  @SpyBean
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @Test
  public void shouldSortItemsByTypeAndCallSaveNodeMethodForEachElement() {
    final ImportNodesRequestBodyItem requestBodyItem = Mockito.spy(new ImportNodesRequestBodyItem());

    final List<ImportNodesRequestBodyItem> items = Mockito.spy(new SpiedArrayList<>());
    items.add(requestBodyItem);

    final LocalDateTime updateDate = LocalDateTime.now();

    final ImportNodesRequestBody requestBody = Mockito.spy(new ImportNodesRequestBody());
    requestBody.setItems(items);
    requestBody.setUpdateDate(updateDate);

    Mockito.doNothing()
      .when(nodeService)
      .saveNode(
        ArgumentMatchers.eq(requestBodyItem),
        ArgumentMatchers.eq(updateDate));

    nodeService.importNodes(requestBody);

    Mockito.verify(requestBody, Mockito.times(1))
      .getUpdateDate();

    Mockito.verify(requestBody, Mockito.times(1))
      .getItems();

    Mockito.verify(items, Mockito.times(1))
      .sort(ArgumentMatchers.any(Comparator.class));

    Mockito.verify(items, Mockito.times(1))
      .forEach(ArgumentMatchers.any(Consumer.class));

    Mockito.verify(nodeService, Mockito.times(1))
      .saveNode(
        ArgumentMatchers.eq(requestBodyItem),
        ArgumentMatchers.eq(updateDate));
  }
}
