package gng.learning.accountapi.customException;

public class InsufficientFundException extends RuntimeException
{
    public InsufficientFundException(String message) {
        super(message);
    }
}
