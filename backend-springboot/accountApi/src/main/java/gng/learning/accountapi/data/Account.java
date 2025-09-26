package gng.learning.accountapi.data;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "accounts")
public class Account {


    public Account() {
    }

    public Account(UUID userId, AccountType accountType) {
        this.userId = userId;
        this.accountType = accountType;
        this.balance = 0;
    }

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(name = "balance",  nullable = false)
    private Integer balance;

    @Column(name = "reservedBalance",  nullable = false)
    private Integer reservedBalance = 0;



    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    // to avoid infinite recursive in json conversion
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Set<Transaction> transactions = new HashSet<>();




    public void addTransaction(Transaction newTransaction) {
        transactions.add(newTransaction);
        newTransaction.setAccount(this);
    }

    public void removeTransaction(Transaction newTransaction) {
        transactions.remove(newTransaction);
        newTransaction.setAccount(null);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Integer getReservedBalance() {
        return reservedBalance;
    }
    public void setReservedBalance(Integer reservedBalance) {
        this.reservedBalance = reservedBalance;
    }
}
