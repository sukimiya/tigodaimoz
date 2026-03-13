package io.e2x.tigor.tigorgateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RouteCacheService {

    private static final Logger logger = Logger.getLogger(RouteCacheService.class.getName());
    private static final String ROUTES_CACHE_KEY = "gateway:routes:all";
    private static final String ROUTE_CACHE_KEY_PREFIX = "gateway:route:";

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RouteCacheService(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 从Redis缓存中读取所有路由配置
     * @return 路由配置列表
     */
    public Mono<List<RouteDefinition>> getRoutesFromCache() {
        return redisTemplate.opsForValue().get(ROUTES_CACHE_KEY)
                .flatMap(json -> {
                    try {
                        List<RouteDefinition> routes = objectMapper.readValue(json, 
                                objectMapper.getTypeFactory().constructCollectionType(List.class, RouteDefinition.class));
                        logger.log(Level.INFO, "Routes loaded from Redis cache: {0}", routes.size());
                        return Mono.just(routes);
                    } catch (JsonProcessingException e) {
                        logger.log(Level.SEVERE, "Error parsing routes from Redis cache", e);
                        return Mono.empty();
                    }
                })
                .onErrorResume(e -> {
                    logger.log(Level.SEVERE, "Error reading routes from Redis", e);
                    return Mono.empty();
                });
    }

    /**
     * 将路由配置缓存到Redis
     * @param routes 路由配置列表
     * @return 操作结果
     */
    public Mono<Void> cacheRoutes(List<RouteDefinition> routes) {
        try {
            String json = objectMapper.writeValueAsString(routes);
            return redisTemplate.opsForValue().set(ROUTES_CACHE_KEY, json)
                    .doOnSuccess(success -> {
                        if (success) {
                            logger.log(Level.INFO, "Routes cached to Redis: {0}", routes.size());
                        } else {
                            logger.log(Level.WARNING, "Failed to cache routes to Redis");
                        }
                    })
                    .doOnError(e -> logger.log(Level.SEVERE, "Error caching routes to Redis", e))
                    .then();
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing routes", e);
            return Mono.empty();
        }
    }

    /**
     * 从Redis缓存中删除路由配置
     * @return 操作结果
     */
    public Mono<Void> clearRoutesCache() {
        return redisTemplate.delete(ROUTES_CACHE_KEY)
                .doOnSuccess(deleted -> {
                    if (deleted > 0) {
                        logger.log(Level.INFO, "Routes cache cleared");
                    }
                })
                .doOnError(e -> logger.log(Level.SEVERE, "Error clearing routes cache", e))
                .then();
    }

    /**
     * 缓存单个路由配置
     * @param route 路由配置
     * @return 操作结果
     */
    public Mono<Void> cacheRoute(RouteDefinition route) {
        try {
            String json = objectMapper.writeValueAsString(route);
            String key = ROUTE_CACHE_KEY_PREFIX + route.getId();
            return redisTemplate.opsForValue().set(key, json)
                    .doOnSuccess(success -> {
                        if (success) {
                            logger.log(Level.INFO, "Route cached to Redis: {0}", route.getId());
                        }
                    })
                    .doOnError(e -> logger.log(Level.SEVERE, "Error caching route to Redis", e))
                    .then();
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing route", e);
            return Mono.empty();
        }
    }

    /**
     * 从Redis缓存中读取单个路由配置
     * @param routeId 路由ID
     * @return 路由配置
     */
    public Mono<RouteDefinition> getRouteFromCache(String routeId) {
        String key = ROUTE_CACHE_KEY_PREFIX + routeId;
        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> {
                    try {
                        RouteDefinition route = objectMapper.readValue(json, RouteDefinition.class);
                        logger.log(Level.INFO, "Route loaded from Redis cache: {0}", routeId);
                        return Mono.just(route);
                    } catch (JsonProcessingException e) {
                        logger.log(Level.SEVERE, "Error parsing route from Redis cache", e);
                        return Mono.empty();
                    }
                })
                .onErrorResume(e -> {
                    logger.log(Level.SEVERE, "Error reading route from Redis", e);
                    return Mono.empty();
                });
    }

    /**
     * 从Redis缓存中删除单个路由配置
     * @param routeId 路由ID
     * @return 操作结果
     */
    public Mono<Void> deleteRouteFromCache(String routeId) {
        String key = ROUTE_CACHE_KEY_PREFIX + routeId;
        return redisTemplate.delete(key)
                .doOnSuccess(deleted -> {
                    if (deleted > 0) {
                        logger.log(Level.INFO, "Route cache deleted: {0}", routeId);
                    }
                })
                .doOnError(e -> logger.log(Level.SEVERE, "Error deleting route cache", e))
                .then();
    }
}
