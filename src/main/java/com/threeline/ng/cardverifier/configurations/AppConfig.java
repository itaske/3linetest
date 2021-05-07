package com.threeline.ng.cardverifier.configurations;

import com.threeline.ng.cardverifier.exceptions.BadRequestException;
import com.threeline.ng.cardverifier.exceptions.OperationFailedException;
import com.threeline.ng.cardverifier.filters.AuthorizationFilter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class AppConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .cors()
                .and()
                .addFilter(new AuthorizationFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplateBuilder()
                .errorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                        return !clientHttpResponse.getStatusCode().is2xxSuccessful();
                    }

                    @Override
                    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                        if (clientHttpResponse.getStatusCode().is4xxClientError())
                            throw new BadRequestException(clientHttpResponse.getStatusText());
                        throw new OperationFailedException("Card verification failed");
                    }
                })
                .build();
    }
}
