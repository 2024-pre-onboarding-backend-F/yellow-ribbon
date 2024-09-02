package wanted.ribbon.store.dto;

import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Store;

public record StoreResponseDto(Long storeId, String sigun, String storeName,
                               Category category, String address,
                               double storeLat, double storeLon, double rating) {

    public static StoreResponseDto from(Store store) {
        return new StoreResponseDto(
                store.getStoreId(),
                store.getSigun(),
                store.getStoreName(),
                store.getCategory(),
                store.getAddress(),
                store.getStoreLat(),
                store.getStoreLon(),
                store.getRating()
        );
    }
}
