package io.e2x.tigor.tigorgateway.config;

import io.e2x.tigor.tigorgateway.dal.dataobject.RouteDefinitionEntity;
import io.e2x.tigor.tigorgateway.dal.mysql.RouteDefinitionRepository;
import io.e2x.tigor.tigorgateway.service.RouteCacheService;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RouteCacheUpdateScheduler {

    private static final Logger logger = Logger.getLogger(RouteCacheUpdateScheduler.class.getName());
    private final RouteDefinitionRepository routeDefinitionRepository;
    private final RouteCacheService routeCacheService;
    private final DatabaseRouteDefinitionLocator databaseRouteDefinitionLocator;

    public RouteCacheUpdateScheduler(RouteDefinitionRepository routeDefinitionRepository, 
                                   RouteCacheService routeCacheService, 
                                   DatabaseRouteDefinitionLocator databaseRouteDefinitionLocator) {
        this.routeDefinitionRepository = routeDefinitionRepository;
        this.routeCacheService = routeCacheService;
        this.databaseRouteDefinitionLocator = databaseRouteDefinitionLocator;
    }

    /**
     * 定时更新路由缓存
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟
    public void updateRouteCache() {
        logger.log(Level.INFO, "Updating route cache...");
        routeDefinitionRepository.findAll()
                .map(this::convertToRouteDefinition)
                .collectList()
                .subscribe(routes -> {
                    routeCacheService.cacheRoutes(routes)
                            .subscribe(
                                    null, 
                                    error -> logger.log(Level.SEVERE, "Error updating route cache", error),
                                    () -> logger.log(Level.INFO, "Route cache updated successfully: {0} routes", (Object) routes.size())
                            );
                }, error -> {
                    logger.log(Level.SEVERE, "Error reading routes from database", error);
                });
    }

    /**
     * 手动触发路由缓存更新
     */
    public void triggerRouteCacheUpdate() {
        updateRouteCache();
    }

    /**
     * 将RouteDefinitionEntity转换为RouteDefinition
     * @param entity 路由定义实体
     * @return 路由定义
     */
    private RouteDefinition convertToRouteDefinition(RouteDefinitionEntity entity) {
        return databaseRouteDefinitionLocator.convertToRouteDefinition(entity);
    }
}
