package wanted.ribbon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.dto.PopularStoreListResponseDto;
import wanted.ribbon.store.dto.StoreDetailResponseDto;
import wanted.ribbon.store.dto.StoreListResponseDto;
import wanted.ribbon.store.service.StoreService;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<StoreListResponseDto> getStoreList(@RequestParam(value = "lat") double lat,
                                                             @RequestParam(value = "lon") double lon,
                                                             @RequestParam(value = "range") double range,
                                                             @RequestParam(value = "orderBy", defaultValue = "distance", required = false) String orderBy) {
        StoreListResponseDto storeList = storeService.findStores(lat, lon, range, orderBy);
        if (storeList.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(storeList);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailResponseDto> getStoreDetail(@PathVariable Long storeId) {
        StoreDetailResponseDto responseDto = storeService.getStoreDetail(storeId);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/popular")
    public ResponseEntity<PopularStoreListResponseDto> getPopularStoreList(@RequestParam(required = false) Category category) {
        PopularStoreListResponseDto responseDto;
        if(category != null) {
            responseDto = storeService.findPopularCategoryStores(category);
        } else {
            responseDto = storeService.popularStores();
        }
        return ResponseEntity.ok().body(responseDto);
    }
}
