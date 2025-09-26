package gng.learning.sharedLibrary.rabbitMqMessage;

import java.io.Serializable;

/**
 *
 */
public class PaymentRequest implements Serializable {

    private String userId;

    private String accountId;

    private String transactionId;

    private Integer amount;

    private String description;

    public PaymentRequest() {
    }

    public PaymentRequest(String userId, String accountId, String transactionId, Integer amount, String description) {
        this.userId = userId;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.description = description;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "userId='" + userId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}
