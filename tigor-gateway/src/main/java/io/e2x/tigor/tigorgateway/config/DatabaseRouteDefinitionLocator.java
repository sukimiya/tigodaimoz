package io.e2x.tigor.tigorgateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.e2x.tigor.tigorgateway.dal.dataobject.RouteDefinitionEntity;
import io.e2x.tigor.tigorgateway.dal.mysql.RouteDefinitionRepository;
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

    public DatabaseRouteDefinitionLocator(ObjectMapper objectMapper, RouteDefinitionRepository routeDefinitionRepository) {
        this.routeDefinitionRepository = routeDefinitionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        logger.log(Level.INFO, "Loading routes from database...");
        List<RouteDefinitionEntity> entities = routeDefinitionRepository.findAll();
        List<RouteDefinition> definitions = entities.stream()
                .map(this::convertToRouteDefinition)
                .toList();
        logger.log(Level.INFO, "Loaded " + definitions.size() + " routes from database.");
        return Flux.fromIterable(definitions);
    }

    private RouteDefinition convertToRouteDefinition(RouteDefinitionEntity entity) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(entity.getId());
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