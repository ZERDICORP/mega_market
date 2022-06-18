package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.entity.Offer;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.OfferRepository;
import com.yandex_backend_school.mega_market.service.OfferService;
import java.util.Date;
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
 * @created 18/06/2022 - 6:48 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SaveOfferUnitTest {
  @Autowired
  private OfferService offerService;

  @MockBean
  private OfferRepository offerRepository;

  @Test
  public void shouldSaveOffer() {
    final ImportsRequestBodyItem requestBodyItem = Mockito.spy(new ImportsRequestBodyItem());

    offerService.save(requestBodyItem, new Date());

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getId();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getName();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getParentId();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getPrice();

    Mockito.verify(offerRepository, Mockito.times(1))
      .save(ArgumentMatchers.any(Offer.class));
  }
}
