package com.springboot.janchi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig implements WebMvcConfigurer {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .followRedirect(true);

        return builder
                .baseUrl("https://api.data.go.kr") // 공공데이터 API 기본 URL
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean("geminiRestTemplate")
    public RestTemplate geminiRestTemplate() {
        return new RestTemplate();
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:5173",
                        "https://janchi.pics", 
                        "http://52.79.249.187"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/ 로 시작하는 URL 요청이 오면
        registry.addResourceHandler("/images/**")

                .addResourceLocations("file:/home/ec2-user/app/images/");
    }
}
