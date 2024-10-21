package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "사용자 프로필 수정 요청 DTO")
public record UpdateProfileRequest(
        @Schema(description = "경도", example = "37.5100")
        double lat,

        @Schema(description = "위도", example = "127.0000")
        double lon,

        @Schema(description = "점심 추천 기능 여부", example = "false")
        boolean recommend
) { }
