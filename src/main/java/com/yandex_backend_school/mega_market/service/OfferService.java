package com.yandex_backend_school.mega_market.service;

import com.yandex_backend_school.mega_market.entity.Offer;
import com.yandex_backend_school.mega_market.pojo.ImportsRequestBodyItem;
import com.yandex_backend_school.mega_market.repository.OfferRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 6:21 PM
 */

@Service
public class OfferService {
  private final OfferRepository offerRepository;

  @Autowired
  public OfferService(OfferRepository offerRepository) {
    this.offerRepository = offerRepository;
  }

  public void save(ImportsRequestBodyItem requestBodyItem, Date updateDate) {
    offerRepository.save(new Offer(
      requestBodyItem.getId(),
      requestBodyItem.getName(),
      requestBodyItem.getParentId(),
      requestBodyItem.getPrice(),
      updateDate));
  }
}
