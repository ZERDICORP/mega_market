package com.yandex_backend_school.mega_market.service;

import com.yandex_backend_school.mega_market.entity.Category;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 6:23 PM
 */

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  @Autowired
  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public void save(ImportsRequestBodyItem requestBodyItem) {
    categoryRepository.save(new Category(
      requestBodyItem.getId(),
      requestBodyItem.getName(),
      requestBodyItem.getParentId()));
  }
}
