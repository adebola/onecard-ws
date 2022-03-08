package io.factorialsystems.msscprovider.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConstants {
    @Value("${app.local.host.url}")
    private String LOCAL_URL;

    @Value("${app.name}")
    public String APP_NAME;

    @Value("${app.desc}")
    public String APP_DESCRIPTION;

    @Value("${app.version}")
    public String APP_VERSION;

    @Value("${app.author}")
    public String APP_AUTHOR;

    @Value("${app.url}")
    public String APP_URL;

    @Value("${app.email}")
    public String APP_EMAIL;

    @Value("${app.license}")
    public String APP_LICENSE_URL;

    private AppConstants() {
    }

}
