package com.yandex_backend_school.mega_market.service;

import com.yandex_backend_school.mega_market.pojo.ImportsRequestBody;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBodyItem;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 12:48 PM
 */

@Service
public class ShopUnitService {
  private final OfferService offerService;
  private final CategoryService categoryService;

  @Autowired
  public ShopUnitService(OfferService offerService, CategoryService categoryService) {
    this.offerService = offerService;
    this.categoryService = categoryService;
  }

  public void imports(ImportsRequestBody importsRequestBody) {
    final List<ImportsRequestBodyItem> requestBodyItems = importsRequestBody.getItems();
    for (ImportsRequestBodyItem requestBodyItem : requestBodyItems) {
      switch (requestBodyItem.getType()) {
        case OFFER:
          offerService.save(requestBodyItem, importsRequestBody.getUpdateDate());
          break;

        case CATEGORY:
          categoryService.save(requestBodyItem);
          break;
      }
    }
  }
}
