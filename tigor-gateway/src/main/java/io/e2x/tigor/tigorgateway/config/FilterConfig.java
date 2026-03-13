package io.e2x.tigor.tigorgateway.config;

import io.e2x.tigor.tigorgateway.data.CustomTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class FilterConfig {

    @Autowired
    private CustomTenantIdentifierResolver customTenantIdentifierResolver;

    @Bean
    public WebFilter tenantFilter() {
        return customTenantIdentifierResolver;
    }
}
