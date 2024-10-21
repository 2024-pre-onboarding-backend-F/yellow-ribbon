package wanted.ribbon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "맛집 평가 생성 요청 DTO")
public record CreateReviewRequestDto(
        @Schema(description = "평가 점수", example = "5") int score,
        @Schema(description = "평가 내용", example = "아주 좋았어요") String content) {
}
