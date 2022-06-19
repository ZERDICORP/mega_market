package com.yandex_backend_school.mega_market.repository;

import com.yandex_backend_school.mega_market.entity.Node;
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
  @Query("SELECT n FROM node n WHERE n.parent_id = :parent_id")
  List<Node> findAllByParentId(String parent_id);
}
