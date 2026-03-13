package io.e2x.tigor.tigorgateway.dal.mysql;

import io.e2x.tigor.tigorgateway.dal.dataobject.RouteDefinitionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface RouteDefinitionRepository extends R2dbcRepository<RouteDefinitionEntity, Integer> {
}