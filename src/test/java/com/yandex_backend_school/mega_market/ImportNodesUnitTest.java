package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportNodesRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.util.Date;
import java.util.List;
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

  @Test
  public void shouldSaveNodes() {
    final ImportNodesRequestBodyItem requestBodyItem = Mockito.spy(new ImportNodesRequestBodyItem());
    requestBodyItem.setType(Type.CATEGORY);

    final Date updateDate = new Date();

    final ImportNodesRequestBody requestBody = Mockito.spy(new ImportNodesRequestBody());
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(updateDate);

    nodeService.importNodes(requestBody);

    Mockito.verify(requestBody, Mockito.times(1))
      .getItems();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getId();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getName();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getParentId();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getPrice();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getType();

    Mockito.verify(requestBody, Mockito.times(1))
      .getUpdateDate();

    Mockito.verify(nodeRepository, Mockito.times(1))
      .saveAll(ArgumentMatchers.anyList());
  }
}
