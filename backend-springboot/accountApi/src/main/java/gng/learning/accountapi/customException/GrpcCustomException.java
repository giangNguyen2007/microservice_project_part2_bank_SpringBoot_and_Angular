package gng.learning.accountapi.customException;

public class GrpcCustomException extends RuntimeException {

    public GrpcCustomException (String message) {
        super(message);
    }
}
