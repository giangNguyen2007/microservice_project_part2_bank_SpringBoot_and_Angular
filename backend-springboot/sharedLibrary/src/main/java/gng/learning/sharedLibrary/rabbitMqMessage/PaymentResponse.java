package gng.learning.sharedLibrary.rabbitMqMessage;

import java.util.UUID;

public class PaymentResponse {

    private String userId;

    private String accountId;

    private String transactionId;

    private String paymentId;

    private Boolean success;

    private String error;

    public PaymentResponse() {
    }

    public PaymentResponse(String userId, String accountId, String transactionId, String paymentId, Boolean success, String error) {
        this.userId = userId;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.paymentId = paymentId;
        this.success = success;
        this.error = error;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "userId='" + userId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", paymentRequestId='" + transactionId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", success=" + success +
                ", error='" + error + '\'' +
                '}';
    }
}
