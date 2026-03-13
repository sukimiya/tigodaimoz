package io.e2x.tigor.tigorgateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootTest
@ActiveProfiles("test")
public class RouteCacheServiceTest {

    private static final Logger logger = Logger.getLogger(RouteCacheServiceTest.class.getName());

    @Autowired
    private RouteCacheService routeCacheService;

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // 清理缓存
        routeCacheService.clearRoutesCache().subscribe();
    }

    @Test
    public void testRouteCache() {
        logger.log(Level.INFO, "Testing route cache functionality...");

        // 第一次加载，应该从数据库读取并缓存
        logger.log(Level.INFO, "First route load (should load from database)");
        routeDefinitionLocator.getRouteDefinitions()
                .collectList()
                .subscribe(routes -> {
                    logger.log(Level.INFO, "Loaded {0} routes from database", routes.size());

                    // 第二次加载，应该从缓存读取
                    logger.log(Level.INFO, "Second route load (should load from cache)");
                    routeDefinitionLocator.getRouteDefinitions()
                            .collectList()
                            .subscribe(cachedRoutes -> {
                                logger.log(Level.INFO, "Loaded {0} routes from cache", cachedRoutes.size());
                                assert routes.size() == cachedRoutes.size();
                                logger.log(Level.INFO, "Route cache test passed!");
                            });
                });

        // 等待测试完成
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
