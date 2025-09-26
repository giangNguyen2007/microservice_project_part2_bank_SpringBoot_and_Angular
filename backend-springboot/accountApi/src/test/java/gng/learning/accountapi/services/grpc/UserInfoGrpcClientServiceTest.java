package gng.learning.accountapi.services.grpc;

import gng.learning.accountapi.configs.GrpcClientConfig;
import gng.learning.accountapi.customException.GrpcCustomException;
import gng.learning.accountapi.customProperties.CustomGrpcProperties;
import gng.learning.grpc.UserInfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(

        classes = {
                CustomGrpcProperties.class,
                GrpcClientConfig.class,
                UserInfoGrpcClientService.class
        }
)

class UserInfoGrpcClientServiceTest {

    @Autowired
    private UserInfoGrpcClientService _clientService;

    @Test
    void getUserInfo_validUserId() {

        // correct userId, copied from db
         UUID myUserId = UUID.fromString("df220d15-a780-4788-874b-cb003295304a");

        UserInfoResponse myUser =  assertDoesNotThrow(() -> {
            return _clientService.getUserInfo(myUserId);
        });

         System.out.println(myUser.getUserName());

         assertTrue(myUser.getFound());

    }

    @Test
    void getUserInfo_invalidUserId() {

        // correct userId, copied from db
        UUID myUserId = UUID.fromString("df220d15-a780-4788-874b-cb003295324a");

        GrpcCustomException e =  assertThrows( GrpcCustomException.class, () -> {
            _clientService.getUserInfo(myUserId);
        });

        System.out.println(e.getMessage());

    }
}