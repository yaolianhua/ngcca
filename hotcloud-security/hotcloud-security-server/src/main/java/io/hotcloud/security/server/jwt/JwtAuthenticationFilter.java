package io.hotcloud.security.server.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtVerifier jwtVerifier;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtVerifier jwtVerifier, UserDetailsService userDetailsService) {
        this.jwtVerifier = jwtVerifier;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorization.startsWith("Bearer") ||
                authorization.startsWith("bearer")) {

            String sign = authorization.substring(7);
            if (!jwtVerifier.valid(sign)) {
                log.warn("Authorization [Bearer Token] invalid ");
                filterChain.doFilter(request, response);
                return;
            }

            Map<String, Object> attributes = jwtVerifier.retrieveAttributes(sign);
            String username = (String) attributes.get("username");
            if (!StringUtils.hasText(username)) {
                log.warn("Authorization [username null] invalid");
                filterChain.doFilter(request, response);
                return;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                log.info("Authenticated successfully");
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                log.warn("Authorization failed [retrieve user null]");
                filterChain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        }

        filterChain.doFilter(request, response);
    }
}
