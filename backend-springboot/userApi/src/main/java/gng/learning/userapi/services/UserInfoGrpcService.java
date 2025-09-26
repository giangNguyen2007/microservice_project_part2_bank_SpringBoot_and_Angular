package gng.learning.userapi.services;

import gng.learning.grpc.*;
import gng.learning.userapi.data.UserModel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class UserInfoGrpcService extends UserInfoServiceGrpc.UserInfoServiceImplBase {

    private final UserDataService _userDataService;

    @Autowired
    public UserInfoGrpcService(UserDataService _userDataService) {
        this._userDataService = _userDataService;
    }

    @Override
    public void getUserInfo(UserInfoRequest request, StreamObserver<UserInfoResponse> responseObserver) {

        UUID userId;
        UserModel myUser;

        try {

            userId = UUID.fromString(request.getUserId());

             myUser = _userDataService.getUserById(userId);

        } catch (IllegalArgumentException e)
        {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException());

            return;
        }

        UserInfoResponse response = UserInfoResponse.newBuilder()
                        .setFound(true)
                        .setUserName(myUser.getName())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}


