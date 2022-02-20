package io.factorialsystems.msscprovider.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Slf4j
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
//
//        http.authorizeRequests()
//                .antMatchers("/api/v1/serviceprovider","/api/v1/serviceprovider/*","/api/v1/serviceprovider/**").permitAll()
//                .antMatchers("/api/v1/recharge","/api/v1/recharge/*","/api/v1/recharge/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .oauth2ResourceServer()
//                .jwt().jwtAuthenticationConverter(converter);
//    }
//
//    @Override
//    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web)  {
//        web.ignoring()
//                .antMatchers("/api/v1/serviceprovider","/api/v1/serviceprovider/**", "/api/v1/recharge", "/api/v1/recharge/**");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/v1/serviceprovider","/api/v1/serviceprovider/*","/api/v1/serviceprovider/**").permitAll()
                .antMatchers("/api/v1/recharge","/api/v1/recharge/*","/api/v1/recharge/**").permitAll()
                .anyRequest().permitAll()
                .and().csrf().disable();
    }

}
