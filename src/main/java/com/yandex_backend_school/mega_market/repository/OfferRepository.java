package com.yandex_backend_school.mega_market.repository;

import com.yandex_backend_school.mega_market.entity.Offer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 2:23 PM
 */

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
}
