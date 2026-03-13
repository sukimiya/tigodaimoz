package io.e2x.tigor.tigorgateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.e2x.tigor.tigorgateway.dal.dataobject.RouteDefinitionEntity;
import io.e2x.tigor.tigorgateway.dal.mysql.RouteDefinitionRepository;
import io.e2x.tigor.tigorgateway.service.RouteCacheService;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class DatabaseRouteDefinitionLocator implements RouteDefinitionLocator {
    private final Logger logger = Logger.getLogger(DatabaseRouteDefinitionLocator.class.getName());
    private final RouteDefinitionRepository routeDefinitionRepository;
    private final ObjectMapper objectMapper;
    private final RouteCacheService routeCacheService;

    public DatabaseRouteDefinitionLocator(ObjectMapper objectMapper, RouteDefinitionRepository routeDefinitionRepository, RouteCacheService routeCacheService) {
        this.routeDefinitionRepository = routeDefinitionRepository;
        this.objectMapper = objectMapper;
        this.routeCacheService = routeCacheService;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        logger.log(Level.INFO, "Loading routes...");
        // 首先尝试从Redis缓存读取
        return routeCacheService.getRoutesFromCache()
                .flatMapMany(routes -> {
                    if (!routes.isEmpty()) {
                        logger.log(Level.INFO, "Routes loaded from Redis cache");
                        return Flux.fromIterable(routes);
                    } else {
                        // 从数据库读取并缓存
                        logger.log(Level.INFO, "Loading routes from database...");
                        return routeDefinitionRepository.findAll()
                                .map(this::convertToRouteDefinition)
                                .collectList()
                                .flatMapMany(routesList -> {
                                    // 缓存到Redis
                                    routeCacheService.cacheRoutes(routesList)
                                            .subscribe();
                                    logger.log(Level.INFO, "Loaded routes from database and cached to Redis");
                                    return Flux.fromIterable(routesList);
                                });
                    }
                })
                .onErrorResume(e -> {
                    // 缓存读取失败时从数据库读取
                    logger.log(Level.SEVERE, "Error loading routes from cache, falling back to database", e);
                    return routeDefinitionRepository.findAll()
                            .map(this::convertToRouteDefinition)
                            .doOnComplete(() -> logger.log(Level.INFO, "Loaded routes from database"));
                });
    }

    public RouteDefinition convertToRouteDefinition(RouteDefinitionEntity entity) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(entity.getId().toString());
        definition.setUri(URI.create(entity.getUri()));
        definition.setPredicates(parseJson(entity.getPredicates(), PredicateDefinition.class));
        definition.setFilters(parseJson(entity.getFilters(), FilterDefinition.class));
        definition.setOrder(entity.getOrder());
        return definition;
    }

    // 使用 Jackson ObjectMapper 解析 JSON 字符串为 List<T>
    private <T> List<T> parseJson(String json, Class<T> type) {
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}