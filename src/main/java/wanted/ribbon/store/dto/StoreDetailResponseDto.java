package wanted.ribbon.store.dto;

import wanted.ribbon.store.domain.Category;

import java.util.List;

public record StoreDetailResponseDto(
        Long storeId,
        String sigun,
        String storeName,
        Category category,
        String address,
        double storeLat,
        double storeLon,
        double rating,
        List<ReviewListResponseDto> reviewList
) {
}
