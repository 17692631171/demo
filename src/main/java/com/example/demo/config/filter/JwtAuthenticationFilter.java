package com.example.demo.config.filter;

import com.example.demo.util.security.JwtUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 获取Authorization头
            String authHeader = request.getHeader("Authorization");

            if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // 验证token
                if (jwtUtil.validateToken(token)) {
                    // 从token中提取用户名
                    String username = jwtUtil.extractUsername(token);

                    // 如果用户名不为空且SecurityContext中没有认证信息
                    if (StringUtils.isNotBlank(username)
                            && SecurityContextHolder.getContext().getAuthentication() == null) {

                        // 加载用户信息
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // 验证token是否有效
                        if (jwtUtil.validateToken(token, userDetails)) {
                            // 创建认证token
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                            // 设置详情
                            authenticationToken.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );

                            // 设置认证信息到SecurityContext
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                            // 设置用户名到请求属性，便于后续使用
                            request.setAttribute("username", username);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("JWT认证失败", e);
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // 排除不需要认证的路径
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/public/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/error")
                || path.equals("/favicon.ico");
    }
}