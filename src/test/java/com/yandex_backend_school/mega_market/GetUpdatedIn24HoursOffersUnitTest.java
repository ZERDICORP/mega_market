package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import com.yandex_backend_school.mega_market.pojo.GetNodesResponseBody;
import com.yandex_backend_school.mega_market.repository.NodeRepository;
import com.yandex_backend_school.mega_market.service.NodeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
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
 * @created 22/06/2022 - 12:47 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetUpdatedIn24HoursOffersUnitTest {
  @Autowired
  private NodeService nodeService;

  @MockBean
  private NodeRepository nodeRepository;

  @Test
  public void shouldFindOffersAndReturnGetNodesResponseBody() {
    final LocalDateTime date = LocalDateTime.now();

    final String offerId = UUID.randomUUID().toString();

    final Node offer = Mockito.spy(new Node());
    offer.setId(offerId);

    final List<Node> foundOffers = new ArrayList<>();
    foundOffers.add(offer);

    Mockito.doReturn(foundOffers)
      .when(nodeRepository)
      .findUpdatedIn24Hours(
        ArgumentMatchers.eq(Type.OFFER),
        ArgumentMatchers.eq(date));

    final GetNodesResponseBody responseBody = nodeService.getUpdatedIn24HoursOffers(date);
    assertNotNull(responseBody);
    assertNotNull(responseBody.getItems());
    assertEquals(1, responseBody.getItems().size());
    assertEquals(offerId, responseBody.getItems().get(0).getId());

    Mockito.verify(nodeRepository, Mockito.times(1))
      .findUpdatedIn24Hours(
        ArgumentMatchers.eq(Type.OFFER),
        ArgumentMatchers.eq(date));

    Mockito.verify(offer, Mockito.times(1))
      .getId();

    Mockito.verify(offer, Mockito.times(1))
      .getName();

    Mockito.verify(offer, Mockito.times(1))
      .getType();

    Mockito.verify(offer, Mockito.times(1))
      .getParentId();

    Mockito.verify(offer, Mockito.times(1))
      .getDate();

    Mockito.verify(offer, Mockito.times(1))
      .getPrice();
  }
}
