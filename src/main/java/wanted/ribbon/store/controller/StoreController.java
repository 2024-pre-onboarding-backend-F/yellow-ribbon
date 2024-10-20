package wanted.ribbon.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.dto.PopularStoreListResponseDto;
import wanted.ribbon.store.dto.RisingPopularStoreListResponseDto;
import wanted.ribbon.store.dto.StoreDetailResponseDto;
import wanted.ribbon.store.dto.StoreListResponseDto;
import wanted.ribbon.store.service.StoreService;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "Store API")
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
    @Operation(summary = "맛집 상세 정보 조회", description = "맛집 상세 정보 조회 시 사용하는 API\n" +
            "- 맛집 모든 필드와 맛집의 평가 상세 리스트 포함")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<StoreDetailResponseDto> getStoreDetail(
            @Parameter(description = "조회할 맛집 식별번호") @PathVariable Long storeId) {
        StoreDetailResponseDto responseDto = storeService.getStoreDetail(storeId);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 맛집 조회", description = "인기 맛집 조회 시 사용하는 API")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<PopularStoreListResponseDto> getPopularStoreList(
            @Parameter(description = "조회할 카테고리") @RequestParam(required = false) Category category,
            @Parameter(description = "조회할 지역") @RequestParam(required = false) String sigun) {
        PopularStoreListResponseDto responseDto;
        if(category != null) {
            responseDto = storeService.findPopularCategoryStores(category);
        } else if(sigun != null) {
            responseDto = storeService.findPopularSigunStores(sigun);
        } else {
            responseDto = storeService.popularStores();
        }
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/rising")
    @Operation(summary = "인기 급상승 맛집 조회", description = "인기 급상승 맛집 조회 시 사용하는 API")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<RisingPopularStoreListResponseDto> getRisingPopularStoreList() {
        RisingPopularStoreListResponseDto responseDto = storeService.findRisingStores();
        return ResponseEntity.ok().body(responseDto);
    }
}
