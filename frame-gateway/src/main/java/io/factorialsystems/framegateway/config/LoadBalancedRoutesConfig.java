package io.factorialsystems.framegateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalancedRoutesConfig {

    @Bean
    public RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("test-service", r -> r.path( "/api/v1/test", "/api/v1/test/**").uri("lb://test-server"))
                .route("voucher-service", r -> r.path("/api/v1/voucher","/api/v1/voucher/**", "/api/v1/batch", "/api/v1/batch/**").uri("lb://voucher-server"))
                .route("provider-service", r -> r.path("/api/v1/provider", "/api/v1/provider/**").uri("lb://provider-server"))
                .build();
    }
}
