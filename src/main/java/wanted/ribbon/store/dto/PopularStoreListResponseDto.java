package wanted.ribbon.store.dto;

import wanted.ribbon.store.domain.Store;

import java.util.List;
import java.util.stream.Collectors;

public record PopularStoreListResponseDto(List<StoreResponseDto> stores) {
    public static PopularStoreListResponseDto fromStoreList(List<Store> storeList) {
        List<StoreResponseDto> storeResponseDto = storeList.stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
        return new PopularStoreListResponseDto(storeResponseDto);
    }
}
