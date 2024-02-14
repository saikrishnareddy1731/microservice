package com.hire10x.CloudGateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
// @EnableWebFluxSecurity because our application is reactive
@EnableWebFluxSecurity 
public class OktaOAuth2WebSecurity {
	
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchange -> exchange
                        .anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .oauth2ResourceServer(OAuth2ResourceServerSpec::jwt);
        return http.build();
    }

}
