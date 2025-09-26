package gng.learning.userapi.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MyGrpcServiceTest {

//    @TestConfiguration
//    static class TestConfig{
//
//        @Bean
//        GrpcServerConfigu inProcessGrpcServerConfigurer() {
//            return serverBuilder -> serverBuilder.inProcessServerName("in-process");
//        }
//
//    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void sayHello() {
    }
}