package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import wanted.ribbon.user.domain.SocialType;

@Schema(title = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "아이디", example = "test1021")
        String id,
        @Schema(description = "비밀번호", example = "tt1021!")
        String password,

        @Schema(description = "소셜 계정 타입", example = "")
        SocialType socialType
) { }
