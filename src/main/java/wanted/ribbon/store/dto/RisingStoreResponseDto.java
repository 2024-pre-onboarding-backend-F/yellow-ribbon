package wanted.ribbon.store.dto;

import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Store;

public record RisingStoreResponseDto(Long storeId, String sigun, String storeName,
                                     Category category, String address,
                                     double storeLat, double storeLon, double rating,
                                     int rank) {
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
