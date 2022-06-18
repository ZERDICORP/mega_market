package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBodyItem;
import com.yandex_backend_school.mega_market.service.CategoryService;
import com.yandex_backend_school.mega_market.service.OfferService;
import com.yandex_backend_school.mega_market.service.ShopUnitService;
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
public class ImportsUnitTest {
  @Autowired
  private ShopUnitService shopUnitService;

  @MockBean
  private OfferService offerService;

  @MockBean
  private CategoryService categoryService;

  @Test
  public void shouldPassRequestBodyItemToCategoryService() {
    final ImportsRequestBodyItem requestBodyItem = Mockito.spy(new ImportsRequestBodyItem());
    requestBodyItem.setType(Type.CATEGORY);

    final Date updateDate = new Date();

    final ImportsRequestBody requestBody = Mockito.spy(new ImportsRequestBody());
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(updateDate);

    shopUnitService.imports(requestBody);

    Mockito.verify(requestBody, Mockito.times(1))
      .getItems();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getType();

    Mockito.verify(offerService, Mockito.times(0))
      .save(
        ArgumentMatchers.eq(requestBodyItem),
        ArgumentMatchers.eq(updateDate));

    Mockito.verify(categoryService, Mockito.times(1))
      .save(ArgumentMatchers.eq(requestBodyItem));
  }

  @Test
  public void shouldPassRequestBodyItemToOfferService() {
    final ImportsRequestBodyItem requestBodyItem = Mockito.spy(new ImportsRequestBodyItem());
    requestBodyItem.setType(Type.OFFER);

    final Date updateDate = new Date();

    final ImportsRequestBody requestBody = Mockito.spy(new ImportsRequestBody());
    requestBody.setItems(List.of(requestBodyItem));
    requestBody.setUpdateDate(updateDate);

    shopUnitService.imports(requestBody);

    Mockito.verify(requestBody, Mockito.times(1))
      .getItems();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getType();

    Mockito.verify(offerService, Mockito.times(1))
      .save(
        ArgumentMatchers.eq(requestBodyItem),
        ArgumentMatchers.eq(updateDate));

    Mockito.verify(categoryService, Mockito.times(0))
      .save(ArgumentMatchers.eq(requestBodyItem));
  }
}
