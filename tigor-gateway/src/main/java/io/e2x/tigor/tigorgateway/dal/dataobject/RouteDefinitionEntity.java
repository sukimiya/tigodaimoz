package io.e2x.tigor.tigorgateway.dal.dataobject;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Data
@Table(name = "gateway_route")
public class RouteDefinitionEntity {
    @Id
    private Integer id;
    private String routeId;
    private String uri;
    private String predicates;
    private String filters;
    private Integer order;
}
