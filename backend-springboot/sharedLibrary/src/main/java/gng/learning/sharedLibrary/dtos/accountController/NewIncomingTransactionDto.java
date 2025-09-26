package gng.learning.sharedLibrary.dtos.accountController;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NewIncomingTransactionDto(

        @NotNull
        UUID userId,

        @NotNull
        UUID accountId,

        @NotBlank
        @Min(value = 1, message = "Amount must be greater than zero")
        Integer amount,

        @NotBlank
        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        @NotNull
        UUID destinationAccountId,

        // url for later notification when transaction is executed
        @NotBlank
        String sourceUrl
) { }
