package gng.learning.sharedLibrary.dtos.accountController;

import java.util.UUID;

public record CreateAccountDto(
        UUID userId,
        String accountType

) { }


