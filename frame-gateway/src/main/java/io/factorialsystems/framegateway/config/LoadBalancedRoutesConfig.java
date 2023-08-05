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
                .route("voucher-service", r -> r.path("/api/v1/voucher","/api/v1/voucher/**",
                                "/api/v1/batch", "/api/v1/batch/**", "/api/v1/cluster", "/api/v1/cluster/**")
                        .uri("lb://voucher-server"))
                .route("provider-service", r -> r.path("/api/v1/provider", "/api/v1/provider/**", "/api/v1/recharge",
                                "/api/v1/recharge/**", "/api/v1/serviceprovider", "/api/v1/serviceprovider/**",
                                "/api/v1/auth-recharge", "/api/v1/auth-recharge/**", "/api/v1/recharge-report", "/api/v1/recharge-report/**")
                        .uri("lb://provider-server"))
                .route("user-service", r -> r.path("/api/v1/user", "/api/v1/user/**", "/api/v1/role", "/api/v1/role/**",
                                "/api/v1/beneficiary", "/api/v1/beneficiary/**", "/api/v1/organization", "/api/v1/organization/**")
                        .uri("lb://user-server"))
                .route("wallet-service", r -> r.path("/api/v1/account", "/api/v1/account/**", "/api/v1/transaction","/api/v1/transaction/**",
                                "/api/v1/kyc", "/api/v1/kyc/**").uri("lb://wallet-server"))
                .route("audit-service", r -> r.path("/api/v1/audit", "/api/v1/audit/**", "/api/v1/contact", "/api/v1/contact/**", "/api/v1/contactus", "/api/v1/contactus/**")
                        .uri("lb://audit-server"))
                .route("report-service", r -> r.path("/api/v1/reports", "/api/v1/reports/**").uri("lb://report-server"))
                .route("payment-service", r -> r.path("/api/v1/pay", "/api/v1/pay/**", "/api/v1/payment", "/api/v1/payment/**")
                        .uri("lb://payment-server"))
                .route("api-user-service", r -> r.path("/api/v1/api-user", "/api/v1/api-user/**", "/api/v2", "/api/v2/**", "/v2/**", "//swagger-ui", "/swagger-ui/**", "/swagger-resources", "/swagger-resources/**","/favicon.ico")
                        .uri("lb://api-user-server"))
                .route("communication-service", r -> r.path("/api/v1/upload", "/api/v1/upload/**", "/api/v1/sms", "/api/v1/sms/**", "/api/v1/mail", "/api/v1/mail/**")
                        .uri("lb://communication-server"))
                .build();
    }
}
