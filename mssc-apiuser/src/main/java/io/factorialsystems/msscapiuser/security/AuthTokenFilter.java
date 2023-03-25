package io.factorialsystems.msscapiuser.security;

import io.factorialsystems.msscapiuser.dao.UserMapper;
import io.factorialsystems.msscapiuser.domain.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@NoArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    public static final String TOKEN_HEADER = "X-TOKEN";
    public static final String SECRET_HEADER = "X-SECRET";

    private static final String SWAGGER_URI = "/v2/api-docs";
    private static final String INVALID_TOKEN_SECRET = "{\"error\": \"Invalid Token and/or Secret\"}";
    private static final String TOKEN_SECRET_NOT_SUPPLIED = "{\"error\": \"Token and Secret must be supplied\"}";

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    Keycloak keycloak;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String uri = request.getRequestURI();

        // Swagger Route
        if (uri.equals(SWAGGER_URI) || uri.startsWith("/swagger-ui") || uri.startsWith("/swagger-resources") || uri.equals("/favicon.ico")) {
            log.info("Swagger Route: {}", request.getRequestURI());
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken (null, "NO-TOKEN", null);

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Requested Route: {}", uri);

        String tokenHeader = request.getHeader(TOKEN_HEADER);
        String secretHeader = request.getHeader(SECRET_HEADER);

        if (tokenHeader == null || tokenHeader.isEmpty() || secretHeader == null || secretHeader.isEmpty()) {
            log.error("Token or Secret not supplied");
            writeErrorResponse(response, TOKEN_SECRET_NOT_SUPPLIED);
            return;
        }

        // Get User from Database
        User user = userMapper.findUserById(tokenHeader);

        if (user == null || user.getSecret() == null) {
            log.error("User {} Not Found", tokenHeader);
            writeErrorResponse(response, INVALID_TOKEN_SECRET);
            return;
        }

        if (!passwordEncoder.matches(secretHeader, user.getSecret())) {
            log.error("user Secret does not match: {}",  secretHeader);
            writeErrorResponse(response, INVALID_TOKEN_SECRET);
            return;
        }

        String token = keycloak.getUserToken(tokenHeader);

        if (token == null) {
            log.error("Unable to get Keycloak Token from Token: {}, Secret: {}", tokenHeader, secretHeader);
            writeErrorResponse(response, INVALID_TOKEN_SECRET);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken (user.getUsername(), token, null);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, String narrative) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(narrative);
        response.getWriter().flush();
    }
}
