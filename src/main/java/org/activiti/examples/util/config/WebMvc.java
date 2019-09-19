package org.activiti.examples.util.config;

import org.activiti.examples.util.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.lang.reflect.Array;
import java.util.Arrays;

@Configuration
public class WebMvc {


    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
           CorsConfiguration config = new CorsConfiguration();
           config.setAllowedMethods(Arrays.asList("POST","GET"));
           source.registerCorsConfiguration("/**", config);
           FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter());
           bean.setOrder(0);
           return bean;
    }
}
