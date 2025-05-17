package ru.senla.socialnetwork.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.senla.socialnetwork.exceptions.auth.JwtAuthException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final UserDetailsService userDetailsService;

  public JwtAuthFilter(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws IOException, ServletException, JwtAuthException {
    try {
      String token = null;
      String header = request.getHeader("Authorization");
      log.debug("Заголовок: Authorization: {}", header);
      if (header != null && header.startsWith("Bearer ")) {
        token = header.substring(7);
        log.debug("Токен, полученный из Authorization token: {}", token);
      }
      if (token != null && JwtUtils.validateToken(token)) {
        String username = JwtUtils.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        log.info("Авторизован пользователь: {} с ролью: {}", username, userDetails.getAuthorities());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
      filterChain.doFilter(request, response);
    } catch (AuthenticationException ex) {
      response.setContentType("application/json");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter()
          .write("{" +
              "\"error\":\"Authorization error\"," +
              "\"message\":\"" + ex.getMessage() + "\"}"
          );
    }
  }
}
