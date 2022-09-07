/*+----------------------------------------------------------------------
 ||
 ||  Class WebSecurityConfiguration
 ||
 ||         Author:  Adebola Omoboya
 ||
 ||        Purpose:  SpringBoot Java Configuration Class for WebSecurity
 ||
 ||  Inherits From:  WebSecurityConfigurerAdapter
 ||
 ||     Interfaces:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||      Constants:  None
 ||
 |+-----------------------------------------------------------------------
 ||
 ||   Constructors:  RequiredArgsConstructor
 ||
 ||  Class Methods:  None
 ||
 ||  Inst. Methods:  authenticationJwtTokenFilter
 ||                  authenticationManagerBean
 ||                  configure(AuthenticationManagerBuilder auth)
 ||                  passwordEncoder
 ||                  configure(HttpSecurity http)
 ||
 ++-----------------------------------------------------------------------*/
package io.factorialsystems.msscapiuser.config;

import io.factorialsystems.msscapiuser.security.AuthEntryPointJwt;
import io.factorialsystems.msscapiuser.security.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authEntryPointJwt).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/api/v2/auth/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
