package wanted.ribbon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Store;

@Schema(title = "인기 급상승 맛집 목록 응답 DTO",
        example = "[{\"storeId\":1,\"sigun\":\"가평군\",\"storeName\":\"한솥도시락가평점\",\"category\":\"Lunch\",\"address\":\"경기도 가평군 가평읍 가화로 88-1, 1층\",\"storeLat\":37.8276878786,\"storeLon\":127.5147837255,\"rating\":4.5,\"rank\":1}," +
                "{\"storeId\":2,\"sigun\":\"가평군\",\"storeName\":\"가평엄마손도시락\",\"category\":\"Lunch\",\"address\":\"경기도 가평군 가평읍 호반로 2605, 풀하우스 주1동 1층\",\"storeLat\":37.8184314186,\"storeLon\":127.5142365074,\"rating\":4.4,\"rank\":2}]")
public record RisingStoreResponseDto(
        @Schema(description = "맛집 식별번호") Long storeId,
        @Schema(description = "시군명") String sigun,
        @Schema(description = "사업장명") String storeName,
        @Schema(description = "위생업태명(분류)") Category category,
        @Schema(description = "소재지도로명주소") String address,
        @Schema(description = "위도") double storeLat,
        @Schema(description = "경도") double storeLon,
        @Schema(description = "평점") double rating,
        @Schema(description = "순위") int rank) {
    public static RisingStoreResponseDto from(Store store, int rank) {
        return new RisingStoreResponseDto(
                store.getStoreId(),
                store.getSigun(),
                store.getStoreName(),
                store.getCategory(),
                store.getAddress(),
                store.getStoreLat(),
                store.getStoreLon(),
                store.getRating(),
                rank
        );
    }
}
