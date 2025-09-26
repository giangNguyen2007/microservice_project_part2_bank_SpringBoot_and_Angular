package gng.learning.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;



@Configuration
public class RouterConfig {

    @Value("${user.api.host}")
    private String userApiHost;

    @Value("${user.api.port}")
    private String userApiPort;

    @Value("${account.api.host}")
    private String accountApiHost;

    @Value("${account.api.port}")
    private String accountApiPort;

    @Bean
    RouterFunction<ServerResponse> userRoutes() {
        return GatewayRouterFunctions.route("route_userApi")
                .GET("/user/**", HandlerFunctions.http())
                .POST("/user/**", HandlerFunctions.http()) // proxy HTTP handler
                .before(BeforeFilterFunctions.uri("http://" + userApiHost + ":" + userApiPort))  // set target URI
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> accountRoutes() {
        return GatewayRouterFunctions.route("route_accountApi")
                .GET("/account/**", HandlerFunctions.http())
                .POST("/account/**", HandlerFunctions.http()) // proxy HTTP handler
                .DELETE("/account/**", HandlerFunctions.http())
                .GET("/transaction/**", HandlerFunctions.http())
                .POST("/transaction/**", HandlerFunctions.http())
                .GET("/incoming-transaction/**", HandlerFunctions.http())
                .POST("/incoming-transaction/**", HandlerFunctions.http())
                .PUT("/incoming-transaction/**", HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri("http://" + accountApiHost + ":" + accountApiPort))  // set target URI
                .build();
    }
}
