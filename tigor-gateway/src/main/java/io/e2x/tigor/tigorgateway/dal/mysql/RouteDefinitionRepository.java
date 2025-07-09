package io.e2x.tigor.tigorgateway.dal.mysql;

import io.e2x.tigor.tigorgateway.dal.dataobject.RouteDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteDefinitionRepository extends JpaRepository<RouteDefinitionEntity, Integer> {

}