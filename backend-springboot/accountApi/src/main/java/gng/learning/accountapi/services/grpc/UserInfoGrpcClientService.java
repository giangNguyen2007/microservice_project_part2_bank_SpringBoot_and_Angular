package gng.learning.accountapi.services.grpc;

import gng.learning.accountapi.customException.GrpcCustomException;
import gng.learning.grpc.UserInfoRequest;
import gng.learning.grpc.UserInfoResponse;
import gng.learning.grpc.UserInfoServiceGrpc;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserInfoGrpcClientService  {


    @Autowired
    private UserInfoServiceGrpc.UserInfoServiceBlockingStub clientStub;

    public UserInfoResponse getUserInfo(UUID userId){

        UserInfoRequest myRequest = UserInfoRequest.newBuilder()
                .setUserId(userId.toString())
                .build();

        try {

            UserInfoResponse myResponse = clientStub.getUserInfo(myRequest);

            return myResponse;
        } catch (StatusRuntimeException e) {

            throw new GrpcCustomException(e.getMessage());
        }


    }
}
