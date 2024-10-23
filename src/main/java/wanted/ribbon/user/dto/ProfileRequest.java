package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "사용자 프로필 조회 요청 DTO")
public record ProfileRequest(
        @Schema(description = "아이디", example = "test1021")
        String id,

        @Schema(description = "경도", example = "37.5128")
        double lat,

        @Schema(description = "위도", example = "127.0057")
        double lon,
        @Schema(description = "점심 추천 기능 여부", example = "true")
        boolean recommend
) { }
