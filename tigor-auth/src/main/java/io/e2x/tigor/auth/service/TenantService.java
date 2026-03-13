package io.e2x.tigor.auth.service;

import io.e2x.tigor.auth.data.CustomTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TenantService {

    @Autowired
    private DatabaseClient databaseClient;

    // 初始化租户Schema
    public Mono<Void> initTenantSchema(String tenantId) {
        return databaseClient.sql("CREATE SCHEMA IF NOT EXISTS " + tenantId)
                .then()
                .onErrorResume(e -> Mono.empty()); // 忽略已存在的错误
    }

    // 初始化所有租户Schema
    public Mono<Void> initAllTenantSchemas(List<String> tenantIds) {
        return Mono.when(tenantIds.stream()
                .map(this::initTenantSchema)
                .toList());
    }

    // 切换到指定租户
    public Mono<Void> switchTenant(String tenantId) {
        CustomTenantIdentifierResolver.setCurrentTenant(tenantId);
        return databaseClient.sql("SET search_path = " + tenantId)
                .then();
    }
}
