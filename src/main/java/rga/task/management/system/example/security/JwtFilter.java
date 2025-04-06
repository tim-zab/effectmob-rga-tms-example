package rga.task.management.system.example.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtAccessProvider accessProvider;

    private String extractJwtFromRequest(HttpServletRequest servletRequest) {
        var authHeader = servletRequest.getHeader(AUTHORIZATION);
        return (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER))
                ? authHeader.substring(BEARER.length()) : null;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        final String jwt = extractJwtFromRequest((HttpServletRequest) servletRequest);

        if (jwt != null && accessProvider.validateJwt(jwt)) {

            final Claims jwtClaims = accessProvider.getJwtClaims(jwt);
            final CustomAuthentication jwtAuth = accessProvider.getJwtAuthentication(jwtClaims);

            jwtAuth.setAuthenticated(true);

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(jwtAuth);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
