package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(title = "회원가입 응답 DTO")
public record SignUpResponse(
        @Schema(description = "응답 성공 여부", example = "200 - 회원가입 성공")
        String message,

        @Schema(description = "userId의 UUID 값", example = "5268b7a2-86ed-4539-abde-194f1ff3b260")
        UUID userId,

        @Schema(description = "아이디", example = "test1021")
        String id

) { }
