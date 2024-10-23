package wanted.ribbon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import wanted.ribbon.store.domain.Category;

import java.util.List;

@Schema(title = "맛집 상세 정보 조회 응답 DTO")
public record StoreDetailResponseDto(
        @Schema(description = "맛집 식별번호", example = "1") Long storeId,
        @Schema(description = "시군명", example = "가평군") String sigun,
        @Schema(description = "사업장명", example = "한솥도시락가평점") String storeName,
        @Schema(description = "위생업태명(분류)", example = "Lunch") Category category,
        @Schema(description = "소재지도로명주소", example = "경기도 가평군 가평읍 가화로 88-1, 1층") String address,
        @Schema(description = "위도", example = "37.8276878786") double storeLat,
        @Schema(description = "경도", example = "127.5147837255") double storeLon,
        @Schema(description = "평점", example = "4.5") double rating,
        @Schema(description = "평가 목록") List<ReviewListResponseDto> reviewList
) {
}
