package gng.learning.accountapi.data;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incomingTransactions")
public class IncomingDebitTransaction {

    public IncomingDebitTransaction() {
    }

    public IncomingDebitTransaction(Integer amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;


    // no one to many relationship, as we don't want to load all incoming transactions with the account
    @Column(name = "accountId", nullable = false)
    private UUID accountId;

    @Column(name = "destinationAccountId", nullable = false)
    private UUID destinationAccountId;

    @Column(name = "isExternalTransaction", nullable = false)
    private boolean isExternalTransaction;

    @Column(name = "amount", nullable = false)
    private Integer amount;



    @Column(name = "description") // TEXT type for longer descriptions
    private String description;

    @Column(name = "validated", nullable = false)
    private Boolean validated = false;



    // url of the source of the transaction (e.g., payment gateway, external system)
    // allows to send notification or query status once validation is done
    @Column(name = "sourceUrl", nullable = false)
    private String sourceUrl;

    @CreationTimestamp // Automatically sets the creation timestamp
    @Column(name = "createdAt", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime createdAt;


    // ============== GETTER and SETTER =========================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(UUID destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public boolean isExternalTransaction() {
        return isExternalTransaction;
    }

    public void setExternalTransaction(boolean externalTransaction) {
        isExternalTransaction = externalTransaction;
    }
}

