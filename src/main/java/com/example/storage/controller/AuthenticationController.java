package com.example.storage.controller;

import com.example.storage.exception.ErrorInputDataException;
import com.example.storage.model.User;
import com.example.storage.tokenUtils.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Validated User user) {
        String login = user.getLogin();
        String password = user.getPassword();
        try {
            // Аутентификация пользователя
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //генерация токена
            String token = jwtTokenUtil.generateToken(login);

            // Создание JSON объекта с токеном
            Map<String, String> response = new HashMap<>();
            response.put("auth-token", token);

            // Возвращение JSON объекта в ResponseEntity
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new ErrorInputDataException("Invalid login or password");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // Получение токена из заголовка Authorization
        String token = request.getHeader("auth-token");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtTokenUtil.invalidateToken(token); // Добавление токена в черный список
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}