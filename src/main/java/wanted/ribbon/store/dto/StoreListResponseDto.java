package wanted.ribbon.store.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import wanted.ribbon.store.domain.Store;

import java.util.List;
import java.util.stream.Collectors;

public record StoreListResponseDto(int totalCount, List<StoreResponseDto> stores) {

    public static StoreListResponseDto fromStoreList(List<Store> storeList) {
        List<StoreResponseDto> storeResponseDto = storeList.stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
        return new StoreListResponseDto(storeList.size(), storeResponseDto);
    }

    // record 타입에 메서드를 추가했을 때 Jackson 라이브러리가 record의 모든 메서드를 직렬화 대상 필드로 간주하기 때문에
    @JsonIgnore // 직렬화 과젱에서 무시하기위해 사용함
    public boolean isEmpty() {
        return stores.isEmpty();
    }
}