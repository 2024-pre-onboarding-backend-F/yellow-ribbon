package wanted.ribbon.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import wanted.ribbon.store.domain.Store;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Schema(title = "인기 급상승 맛집 조회 응답 DTO")
public record RisingPopularStoreListResponseDto(
        @Schema(description = "인기 급상승 맛집 목록") List<RisingStoreResponseDto> stores) {
    public static RisingPopularStoreListResponseDto fromRisingStoreList(List<Store> storeList) {
        List<RisingStoreResponseDto> responseDto = IntStream.range(0, storeList.size())
                .mapToObj(i -> RisingStoreResponseDto.from(storeList.get(i), i+1))
                .collect(Collectors.toList());
        return new RisingPopularStoreListResponseDto(responseDto);
    }
}
