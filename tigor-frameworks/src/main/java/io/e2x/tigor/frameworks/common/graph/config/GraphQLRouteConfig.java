package io.e2x.tigor.frameworks.common.graph.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.gateway.support.HasRouteId;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class GraphQLRouteConfig implements HasRouteId {
    private String routeId;
    private String module;
    private String version;

    public GraphQLRouteConfig (String module, String version) {
        this.module = module;
        this.version = version;
        this.routeId = module + version;
    }
    @Override
    public String getRouteId() {
        return routeId;
    }
    @Override
    public void setRouteId(String routeId){
        this.routeId = routeId;
    }
}
