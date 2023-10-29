package com.example.storage.tokenUtils;

import com.example.storage.model.ErrorResponse;
import com.example.storage.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.storage.handler.IdGenerator.generateId;
import static liquibase.util.StringUtil.isEmpty;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        // Проверяем путь запроса
        if (!request.getRequestURI().equals("/login")) {
            // Get authorization header and validate
            final String header = request.getHeader("auth-token");
            if (isEmpty(header) || !header.startsWith("Bearer ")) {

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                ErrorResponse errorResponse = new ErrorResponse(generateId(), "Invalid Token");
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(response.getWriter(), errorResponse);
                return;
            }

            // Get jwt token and validate
            final String token = header.split(" ")[1].trim();
            if (!jwtTokenUtil.validateToken(token)) {

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                ErrorResponse errorResponse = new ErrorResponse(generateId(), "Invalid Token");
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(response.getWriter(), errorResponse);
                return;
            }

            //  Проверяем, существует ли уже аутентификация
            Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuthentication == null) {

                // Get user identity and set it on the spring security context
                UserDetails userDetails = new UserDetailsImpl(
                        userRepository.findByLogin(jwtTokenUtil.getUsername(token)).orElse(null));

                UsernamePasswordAuthenticationToken
                        authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null,
                        userDetails.getAuthorities()
                );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

}



