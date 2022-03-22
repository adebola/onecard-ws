package io.factorialsystems.framegateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LoadBalancedRoutesConfig {

    @Bean
    public RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("voucher-service",
                        r -> r.path("/api/v1/voucher","/api/v1/voucher/**", "/api/v1/batch",
                                        "/api/v1/batch/**", "/api/v1/cluster", "/api/v1/cluster/**")
                        .uri("lb://voucher-server")
                )
                .route("provider-service", r -> r.path("/api/v1/provider", "/api/v1/provider/**",
                        "/api/v1/recharge", "/api/v1/recharge/**", "**/swagger-ui/**", "**/api/v1/provider/api-docs/",
                        "/api/v1/serviceprovider", "/api/v1/serviceprovider/**",
                        "/api/v1/auth-recharge", "/api/v1/auth-recharge/**")
                        .filters(f->f.rewritePath("/api/v1/provider/api-docs/","/api-docs")
                                .setResponseHeader("Access-Control-Allow-Origin", "*"))//Todo('Remove Cors Access from Header on Production')
                        .uri("lb://provider-server"))
                .route("user-service", r -> r.path("/api/v1/user", "/api/v1/user/**",
                                "/api/v1/role", "/api/v1/role/**",
                                "/api/v1/beneficiary", "/api/v1/beneficiary/**").uri("lb://user-server"))
                .route("wallet-service", r -> r.path("/api/v1/account", "/api/v1/account/**",
                                "/api/v1/transaction","/api/v1/transaction/**").uri("lb://wallet-server"))
                .route("audit-service", r -> r.path("/api/v1/audit", "/api/v1/audit/**").uri("lb://audit-server"))
                .route("report-service", r -> r.path("/api/v1/reports", "/api/v1/reports/**").uri("lb://report-server"))
                .route("payment-service", r -> r.path("/api/v1/pay", "/api/v1/pay/**", "/api/v1/payment",
                                "**/swagger-ui/**", "**/api/v1/payment/api-docs/", "/api/v1/payment/**")
                        .filters(f->f.rewritePath("/api/v1/payment/api-docs/","/api-docs"))
                        .uri("lb://payment-server"))
                .build();
    }
}