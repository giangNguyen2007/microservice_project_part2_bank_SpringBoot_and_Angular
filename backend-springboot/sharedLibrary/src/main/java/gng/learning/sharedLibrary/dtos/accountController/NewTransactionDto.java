package gng.learning.sharedLibrary.dtos.accountController;


import jakarta.validation.constraints.*;

import java.util.UUID;

// DTO for creating new transaction
// represents only #1 External transfer or #2 Internal transfer
// DEBIT transactions in both case
public record NewTransactionDto(

        @NotNull
        UUID accountId,

        @NotNull
        @Min(value = 1, message = "Amount must be greater than zero")
        Integer amount,

        @NotNull
        Boolean isExternalTransaction,

        // could be null for deposit / withdrawal transaction
        UUID destinationAccountId,

        @NotBlank
        @Size(max = 255, message = "Description must be at most 255 characters")
        String description


) { }
