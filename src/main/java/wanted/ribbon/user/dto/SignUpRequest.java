package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(title = "사용자 회원가입 요청 DTO")
public record SignUpRequest(
        @Schema(description = "아이디", example = "test1021")
        @NotBlank
        @Size(max = 50)
        String id,

        @Schema(description = "비밀번호", example = "tt1021!")
        @Size(max = 200)
        String password
) { }
