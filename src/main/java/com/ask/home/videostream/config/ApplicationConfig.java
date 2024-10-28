package com.ask.home.videostream.config;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;

@Configuration
public class ApplicationConfig {

    /**
     * Method to enforce the context path webflux
     * @param serverProperties ServerProperties
     * @return WebFilter.
     */
    @Bean
    public WebFilter contextPathWebFilter(ServerProperties serverProperties) {
        String contextPath = serverProperties.getServlet().getContextPath();
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (request.getURI().getPath().startsWith(contextPath)) {
                return chain.filter(
                        exchange.mutate()
                                .request(request.mutate().contextPath(contextPath).build())
                                .build());
            }
            return chain.filter(exchange);
        };
    }
}
