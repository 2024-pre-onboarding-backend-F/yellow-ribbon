package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Refresh Token 생성 요청 DTO")
public record CreateRefreshTokenRequest(
        @Schema(description = "Refresh Token", example = "a39aa11f-8880-4114-94b4-4af2b4ef26e7")
        String refreshToken
) { }
