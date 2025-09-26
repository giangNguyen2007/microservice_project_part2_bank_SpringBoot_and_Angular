package gng.learning.accountapi.configs;

import gng.learning.accountapi.customProperties.CustomGrpcProperties;
import gng.learning.grpc.UserInfoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class GrpcClientConfig {

//    @Value("${spring.grpc.client.userService.address}")
//    private String userServiceServerAddress;

    private Environment env;
    private static final Logger logger = LoggerFactory.getLogger(GrpcClientConfig.class);

    public GrpcClientConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public ManagedChannel myChannel(CustomGrpcProperties prop){
        //System.out.println("grpc server channel from custom properties = " + prop.getAddress());

        logger.debug("grpc connection :" + env.getProperty("myapp.grpc.server-host") + ":" + env.getProperty("myapp.grpc.server-port"));

        return ManagedChannelBuilder
                .forAddress(env.getProperty("myapp.grpc.server-host"),
                        Integer.parseInt(env.getProperty("myapp.grpc.server-port")))
                .usePlaintext()
                .build();
    }

    @Bean
    public UserInfoServiceGrpc.UserInfoServiceBlockingStub clientStub(ManagedChannel myChannel){
        return UserInfoServiceGrpc.newBlockingStub(myChannel);
    }


}
