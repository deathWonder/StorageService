package com.example.storage;

import com.example.storage.tokenUtils.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestcontainersApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @LocalServerPort
    private int port;


    @Test
    public void testLogin() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем тело запроса
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("login", "user");
        requestBody.put("password", "user");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Отправляем запрос и получаем ответ
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(createURLWithPort("/login"), requestEntity, String.class);

        // Проверяем содержимое ответа
        String responseBody = responseEntity.getBody();
        assertNotNull(responseBody);

        // Преобразуем JSON-строку в объект Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);

        // Получаем значение "auth-token" из карты и выводим его
        String authToken = responseMap.get("auth-token");
        assertNotNull(authToken);

        jwtTokenUtil.validateToken(authToken);
        jwtTokenUtil.invalidateToken(authToken);
        restTemplate.postForEntity(createURLWithPort("/logout"), requestEntity, String.class);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
