package wanted.ribbon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import wanted.ribbon.store.domain.Store;

import java.util.List;
import java.util.stream.Collectors;

@Schema(title = "인기 맛집 조회 응답 DTO")
public record PopularStoreListResponseDto(
        @Schema(description = "맛집 목록") List<StoreResponseDto> stores) {
    public static PopularStoreListResponseDto fromStoreList(List<Store> storeList) {
        List<StoreResponseDto> storeResponseDto = storeList.stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
        return new PopularStoreListResponseDto(storeResponseDto);
    }
}
