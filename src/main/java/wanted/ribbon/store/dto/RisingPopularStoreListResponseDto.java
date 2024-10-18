package wanted.ribbon.store.dto;

import wanted.ribbon.store.domain.Store;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record RisingPopularStoreListResponseDto(List<RisingStoreResponseDto> stores) {
    public static RisingPopularStoreListResponseDto fromRisingStoreList(List<Store> storeList) {
        List<RisingStoreResponseDto> responseDto = IntStream.range(0, storeList.size())
                .mapToObj(i -> RisingStoreResponseDto.from(storeList.get(i), i+1))
                .collect(Collectors.toList());
        return new RisingPopularStoreListResponseDto(responseDto);
    }
}
