package com.yandex_backend_school.mega_market.controller;

import com.yandex_backend_school.mega_market.pojo.ImportsRequestBody;
import com.yandex_backend_school.mega_market.service.ShopUnitService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 12:10 PM
 */

@RestController
@Validated
public class ShopUnitController {
  private final ShopUnitService shopUnitService;

  @Autowired
  public ShopUnitController(ShopUnitService shopUnitService) {
    this.shopUnitService = shopUnitService;
  }

  @PostMapping("/imports")
  public void imports(@Valid @RequestBody ImportsRequestBody importsRequestBody) {
    shopUnitService.imports(importsRequestBody);
  }
}
