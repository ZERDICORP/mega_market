package com.yandex_backend_school.mega_market;

import com.yandex_backend_school.mega_market.entity.Category;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.CategoryRepository;
import com.yandex_backend_school.mega_market.service.CategoryService;
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
 * @created 18/06/2022 - 6:52 PM
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SaveCategoryUnitTest {
  @Autowired
  private CategoryService categoryService;

  @MockBean
  private CategoryRepository categoryRepository;

  @Test
  public void shouldSaveOffer() {
    final ImportsRequestBodyItem requestBodyItem = Mockito.spy(new ImportsRequestBodyItem());

    categoryService.save(requestBodyItem);

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getId();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getName();

    Mockito.verify(requestBodyItem, Mockito.times(1))
      .getParentId();

    Mockito.verify(requestBodyItem, Mockito.times(0))
      .getPrice();

    Mockito.verify(categoryRepository, Mockito.times(1))
      .save(ArgumentMatchers.any(Category.class));
  }
}
