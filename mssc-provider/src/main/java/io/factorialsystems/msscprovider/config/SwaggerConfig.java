package io.factorialsystems.msscprovider.config;

import com.google.common.base.Predicates;
import io.factorialsystems.msscprovider.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.Collections;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {

    private final AppConstants appConstants;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(Predicates.or(RequestHandlerSelectors.basePackage("io.factorialsystems.msscprovider.controller")))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(appConstants.APP_NAME, appConstants.APP_DESCRIPTION, appConstants.APP_VERSION, "Terms of service", new Contact(appConstants.APP_AUTHOR, appConstants.APP_URL, appConstants.APP_EMAIL), "API License", appConstants.APP_LICENSE_URL,
                Collections.emptyList());
    }
}
