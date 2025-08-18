package com.springboot.janchi.janchi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .followRedirect(true);

        return builder
                .baseUrl("https://api.data.go.kr")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
