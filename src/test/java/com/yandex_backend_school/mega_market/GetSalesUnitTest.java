package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.Assert.assertNotNull;
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
 * @created 20/06/2022 - 2:59 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetSalesUnitTest {
  @Autowired
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @Test
  public void shouldFindOffersAndReturnGetSalesResponseBody() {
    final LocalDateTime date = LocalDateTime.now();

    Mockito.doReturn(new ArrayList<>())
      .when(nodeRepository)
      .findUpdatedIn24Hours(
        ArgumentMatchers.eq(Type.OFFER),
        ArgumentMatchers.eq(date));

    final GetNodesResponseBody getNodesResponseBody = nodeService.getUpdatedIn24HoursOffers(date);
    assertNotNull(getNodesResponseBody);
    assertNotNull(getNodesResponseBody.getItems());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findUpdatedIn24Hours(
        ArgumentMatchers.eq(Type.OFFER),
        ArgumentMatchers.eq(date));
  }
}
