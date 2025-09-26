package gng.learning.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PermittedPathConfig {

    // this only serves to register permitted paths as a bean
    // the permitted paths then have to be processed in Jwt Filter

    // only exact matches are allowed by JwtFilter
    // no subpath is allowed, e.g. /user/** won't work
    @Bean(name = "permittedPaths")
    public List<String> permittedPaths() {
        return List.of(
                "/user/login",
                "/user/register",
                "/incoming-transaction"
        );
    }
}
