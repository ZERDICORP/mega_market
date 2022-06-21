package com.yandex_backend_school.mega_market.repository;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 2:23 PM
 */

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {
  @Query(value = "SELECT n.* FROM node n WHERE n.type = :type " +
    "AND n.date >= CAST(:date as DATETIME) - INTERVAL 24 HOUR " +
    "AND n.date <= CAST(:date as DATETIME)", nativeQuery = true)
  List<Node> findUpdatedIn24Hours(Type type, LocalDateTime date);

  @Modifying
  @Query(value = "UPDATE node n SET price = (" +
    "WITH RECURSIVE cte AS(" +
    "SELECT n.* FROM node n WHERE n.id = :nodeId " +
    "UNION ALL " +
    "SELECT n.* FROM node n INNER JOIN CTE c ON c.id = n.parent_id" +
    ") SELECT AVG(c.price) FROM cte c WHERE c.type = 1" +
    ") WHERE n.id = :nodeId", nativeQuery = true)
  void countChildrenAveragePrice(String nodeId);
}
