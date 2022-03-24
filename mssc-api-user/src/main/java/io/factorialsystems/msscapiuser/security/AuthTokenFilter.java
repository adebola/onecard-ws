package io.factorialsystems.msscapiuser.security;

import io.factorialsystems.msscapiuser.dao.UserMapper;
import io.factorialsystems.msscapiuser.domain.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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

        String tokenHeader = request.getHeader(TOKEN_HEADER);
        String secretHeader = request.getHeader(SECRET_HEADER);

        if (tokenHeader == null || tokenHeader.isEmpty() || secretHeader == null || secretHeader.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"Token and Secret must be supplied\"}");
            response.getWriter().flush();
            return;
        }

        // Get User from Database
        User user = userMapper.findByUserId(tokenHeader);

        if (user == null || passwordEncoder.matches(user.getSecret(), secretHeader)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"Invalid Token and/or Secret\"}");
            response.getWriter().flush();
            return;
        }

        String token = keycloak.getUserToken(tokenHeader);

        if (token == null) return;

        Context context = new Context(token);
        TenantContext.setContext(context);
        filterChain.doFilter(request, response);
    }
}
