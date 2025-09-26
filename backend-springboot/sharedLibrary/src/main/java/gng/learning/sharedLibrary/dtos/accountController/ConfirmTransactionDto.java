package gng.learning.sharedLibrary.dtos.accountController;

import java.util.UUID;

public record ConfirmTransactionDto(
        UUID accountId,
        UUID transactionId
) { }
