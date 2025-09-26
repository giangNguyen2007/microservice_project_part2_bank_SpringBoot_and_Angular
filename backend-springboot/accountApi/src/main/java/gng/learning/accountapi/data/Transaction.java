package gng.learning.accountapi.data;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    public Transaction() {
    }

    public Transaction(Integer amount, String description, TransactionStatus status) {
        this.amount = amount;
        this.description = description;
        this.status = status;
    }

    @Id
    @UuidGenerator
    @Column(name = "transactionId", nullable = false)
    private UUID id;


    @ManyToOne()
    @JoinColumn(name = "id", nullable = false)
    // to avoid infinite recursive in json conversion
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Account account;

    @Column(name = "accountId", nullable = false)
    private UUID accountId;

    // null if external transfer / deposit / withdrawal
    // set if internal transfer (between two accounts in the system)
    @Column(name = "destinationAccountId", nullable = true)
    private UUID destinationAccountId;

    // true if transaction is external (e.g., payment gateway), false if internal (between two accounts in the system)
    @Column(name = "isExternalTransaction", nullable = false)
    private boolean isExternalTransaction;

    // CREDIT = incoming money (e.g., deposit, transfer in)
    // DEBIT = outgoing money (e.g., withdrawal, transfer out)
    @Column(name = "isDebit", nullable = false)
    private Boolean isDebit;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "description") // TEXT type for longer descriptions
    private String description;

    // url of the source of the transaction (e.g., payment gateway, external system)
    // non null if the transaction is initiated by an external system
    @Column(name = "sourceUrl")
    private String sourceUrl;

    @Enumerated(EnumType.STRING) // Stores the enum name as a string in the database
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
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

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(UUID destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public boolean getIsExternalTransaction() {
        return isExternalTransaction;
    }

    public void setIsExternalTransaction(boolean externalTransaction) {
        isExternalTransaction = externalTransaction;
    }

    public Boolean getIsDebit() {
        return isDebit;
    }

    public void setIsDebit(Boolean debit) {
        isDebit = debit;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}

