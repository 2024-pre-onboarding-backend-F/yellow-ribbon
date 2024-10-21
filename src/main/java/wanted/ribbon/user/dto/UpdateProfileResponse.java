package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(title = "사용자 프로필 수정 응답 DTO")
public record UpdateProfileResponse(
    @Schema(description = "사용자 프로필 수정 요청 성공 여부 메세지", example = "200 - 위도, 경도, 추천 여부 변경 완료")
    String message,

    @Schema(description = "userId의 UUID 값", example = "5268b7a2-86ed-4539-abde-194f1ff3b260")
    UUID userId,

    @Schema(description = "경도", example = "37.5100")
    double lat,

    @Schema(description = "위도", example = "127.0000")
    double lon,

    @Schema(description = "점심 추천 기능 여부", example = "false")
    boolean recommend
) { }

