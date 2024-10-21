package wanted.ribbon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "시군구 목록 조회 응답 DTO")
public record CityResponseDto(
        @Schema(description = "도", example = "강원") String doSi,
        @Schema(description = "시군구", example = "강릉시") String sgg) {
}

