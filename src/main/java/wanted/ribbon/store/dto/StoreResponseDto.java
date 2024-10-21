package wanted.ribbon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Store;

@Schema(title = "StoreResponseDto (맛집 조회 응답 DTO)")
public record StoreResponseDto(Long storeId, String sigun, String storeName,
                               Category category, String address,
                               double storeLon, double storeLat, double rating, Integer reviewCount) {

    public static StoreResponseDto from(Store store) {
        return new StoreResponseDto(
                store.getStoreId(),
                store.getSigun(),
                store.getStoreName(),
                store.getCategory(),
                store.getAddress(),
                store.getStoreLon(),
                store.getStoreLat(),
                store.getRating(),
                store.getReviewCount()
        );
    }
}
