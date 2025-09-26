package gng.learning.userapi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class JwtExclusionPathConfig {

    @Bean(name = "permittedPaths")
    public List<String> permittedPaths() {
        return List.of(
                "/user/register",
                "/user/login"
        );
    }
}
