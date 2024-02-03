package com.hire10x.OrderService.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hire10x.OrderService.external.decoder.CustomErrorDecoder;

@Configuration
public class FeignConfig {
	
    @Bean
    ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

}
