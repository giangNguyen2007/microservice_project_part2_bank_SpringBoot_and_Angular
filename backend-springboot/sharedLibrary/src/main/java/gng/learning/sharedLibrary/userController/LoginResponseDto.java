package gng.learning.sharedLibrary.userController;

public record LoginResponseDto(
        String token,
        String email,

        String userId,
        String role
) {
}
