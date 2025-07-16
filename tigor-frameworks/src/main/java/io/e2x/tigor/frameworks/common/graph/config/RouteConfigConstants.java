package io.e2x.tigor.frameworks.common.graph.config;

public interface RouteConfigConstants {
    String GRAPHQL_ENDPOINT = "/graphql";
    String GRAPHQL_WEBSOCKET_ENDPOINT = "/subscriptions";
    String GRAPHQL_WEBSOCKET_ENDPOINT_WITH_SUBPROTOCOL = "/subscriptions-transport-ws";
    String HEADER_VERSION_NAME = "version";
    String HEADER_MODULE_NAME = "module";

    String MODULE_VALUE_LOGIN = "login";
    String MODULE_VALUE_LOGOUT = "logout";
    String MODULE_VALUE_REGISTER = "register";
    String [] AUTH_MODULE_IDS = {
            MODULE_VALUE_LOGIN,
            MODULE_VALUE_LOGOUT,
            MODULE_VALUE_REGISTER
    };
    String GRAPHQL_MODULE_ID_INFRA_PREFIX = "graphql_module_infra_";
    String GRAPHQL_MODULE_ID_ERP_PREFIX = "graphql_module_erp_";
    String GRAPHQL_MODULE_ID_PRODUCT_PREFIX = "graphql_module_product_";
    String GRAPHQL_MODULE_ID_MALL_PREFIX = "graphql_module_mall_";
    String GRAPHQL_MODULE_ID_CUSTOMER_PREFIX = "graphql_module_customer_";
    String GRAPHQL_MODULE_ID_PAY_PREFIX = "graphql_module_pay_";
    String GRAPHQL_MODULE_ID_SOCIAL_MEDIA_PREFIX = "graphql_module_social_media_";
    String GRAPHQL_MODULE_ID_SYSTEM_PREFIX = "graphql_module_system_";
    String LOGIN_MODULE_ID_PREFIX = "logout_route_";
    String LOGOUT_MODULE_ID_PREFIX = "logout_route_";
    String REGISTER_MODULE_ID_PREFIX = "register_route_";
    String [] GRAPHQL_MODULE_ID_PREFIXES = {
            GRAPHQL_MODULE_ID_INFRA_PREFIX,
            GRAPHQL_MODULE_ID_ERP_PREFIX,
            GRAPHQL_MODULE_ID_PRODUCT_PREFIX,
            GRAPHQL_MODULE_ID_MALL_PREFIX,
            GRAPHQL_MODULE_ID_CUSTOMER_PREFIX,
            GRAPHQL_MODULE_ID_PAY_PREFIX,
            GRAPHQL_MODULE_ID_SOCIAL_MEDIA_PREFIX,
            GRAPHQL_MODULE_ID_SYSTEM_PREFIX,
            LOGIN_MODULE_ID_PREFIX,
            LOGOUT_MODULE_ID_PREFIX,
            REGISTER_MODULE_ID_PREFIX
    };
}
