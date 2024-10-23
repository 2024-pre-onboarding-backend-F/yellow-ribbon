package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(title = "로그인 응답 DTO")
public record LoginResponse(
        @Schema(description = "userId의 UUID 값", example = "5268b7a2-86ed-4539-abde-194f1ff3b260")
        UUID userId,

        @Schema(description = "Access Token", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ5b3VuZ2pvby5kZXZAZ21haWwuY29tIiwiaWF0IjoxNzI1MzQ0ODM1LCJleHAiOjE3MjUzNTIwMzUsInN1YiI6IndhbnRlZCJ9.OuzQxp9fAfpJeyglQnVrvqt7jUcUVno5YADjQRBjMCs")
        String accessToken,

        @Schema(description = "Refresh Token", example = "a39aa11f-8880-4114-94b4-4af2b4ef26e7")
        String refreshToken
) { }
