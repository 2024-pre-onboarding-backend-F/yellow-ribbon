package wanted.ribbon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "평가 목록 조회 응답 DTO",
        example = "[{\"id\":\"1\",\"score\":5,\"content\":\"아주 좋았어요\"}," +
                "{\"id\":\"2\",\"score\":4,\"content\":\"좋았어요\"}]")
public record ReviewListResponseDto(
        @Schema(description = "평가 식별번호") String id,
        @Schema(description = "평가 점수") int score,
        @Schema(description = "평가 내용") String content) {
}
