package com.yandex_backend_school.mega_market.repository;

import com.yandex_backend_school.mega_market.entity.NodeChange;
import com.yandex_backend_school.mega_market.entity.NodeChangeKey;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 20/06/2022 - 4:18 PM
 */

@Repository
public interface NodeChangeRepository extends JpaRepository<NodeChange, NodeChangeKey> {
  @Query(value = "SELECT nc.* FROM node_change nc WHERE nc.node_id = :nodeId " +
    "AND nc.date >= :dateStart AND nc.date < :dateEnd", nativeQuery = true)
  List<NodeChange> findByNodeIdInDateRange(String nodeId, LocalDateTime dateStart, LocalDateTime dateEnd);

  @Modifying
  @Query(value = "DELETE FROM node_change WHERE node_id IN (:nodeIds)", nativeQuery = true)
  void deleteAllByNodeIdInBatch(List<String> nodeIds);
}