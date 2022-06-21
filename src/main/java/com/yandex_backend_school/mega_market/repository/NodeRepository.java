package com.yandex_backend_school.mega_market.repository;

import com.yandex_backend_school.mega_market.constant.Type;
import com.yandex_backend_school.mega_market.entity.Node;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 18/06/2022 - 2:23 PM
 */

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {
//  List<Node> findByParentId(String parentId);

//  List<Node> deleteByParentId(String parentId);

  @Query(value = "SELECT n.* FROM node n WHERE n.type = :type " +
    "AND n.date >= CAST(:date as DATETIME) - INTERVAL 24 HOUR " +
    "AND n.date <= CAST(:date as DATETIME)", nativeQuery = true)
  List<Node> findUpdatedIn24Hours(Type type, LocalDateTime date);
}
