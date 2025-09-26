package gng.learning.accountapi.configs;

import gng.learning.accountapi.interceptors.UserIdCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private UserIdCheckInterceptor userIdCheckInterceptor;

    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(userIdCheckInterceptor)
                .addPathPatterns("/**");
    }


}
