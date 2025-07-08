package io.e2x.tigor.frameworks.common.graph.config;

public interface GraphRouteConfigConstants {
    String GRAPHQL_ENDPOINT = "/graphql";
    String GRAPHQL_WEBSOCKET_ENDPOINT = "/subscriptions";
    String GRAPHQL_WEBSOCKET_ENDPOINT_WITH_SUBPROTOCOL = "/subscriptions-transport-ws";
    String VERSION_HEADER_NAME = "version";
    String MODULE_HEADER_NAME = "module";

    String GRAPHQL_MODULE_ID_INFRA_PREFIX = "graphql_module_infra_";
    String GRAPHQL_MODULE_ID_ERP_PREFIX = "graphql_module_erp_";
    String GRAPHQL_MODULE_ID_PRODUCT_PREFIX = "graphql_module_product_";
    String GRAPHQL_MODULE_ID_MALL_PREFIX = "graphql_module_mall_";
    String GRAPHQL_MODULE_ID_CUSTOMER_PREFIX = "graphql_module_customer_";
    String GRAPHQL_MODULE_ID_PAY_PREFIX = "graphql_module_pay_";
    String GRAPHQL_MODULE_ID_SOCIAL_MEDIA_PREFIX = "graphql_module_social_media_";
    String GRAPHQL_MODULE_ID_SYSTEM_PREFIX = "graphql_module_system_";
    String [] GRAPHQL_MODULE_ID_PREFIXES = {
            GRAPHQL_MODULE_ID_INFRA_PREFIX,
            GRAPHQL_MODULE_ID_ERP_PREFIX,
            GRAPHQL_MODULE_ID_PRODUCT_PREFIX,
            GRAPHQL_MODULE_ID_MALL_PREFIX,
            GRAPHQL_MODULE_ID_CUSTOMER_PREFIX,
            GRAPHQL_MODULE_ID_PAY_PREFIX,
            GRAPHQL_MODULE_ID_SOCIAL_MEDIA_PREFIX,
            GRAPHQL_MODULE_ID_SYSTEM_PREFIX
    };
}
