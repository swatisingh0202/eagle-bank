package com.eaglebank.feature.auth.filter;

import com.eaglebank.feature.auth.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(1)
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        // Skip JWT check for POST /users (registration) and POST /auth/login
        if ((path.equals("/actuator")) || (path.equals("/v1/users") && method.equals("POST")) ||
                (path.equals("/v1/auth/login") && method.equals("POST"))) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (!jwtProvider.validateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }
        } else if (!request.getRequestURI().contains("/auth/login") &&
                !request.getRequestURI().contains("/users")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing or invalid");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

