package wanted.ribbon.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.ribbon.exception.ErrorResponse;
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
    @Operation(summary = "사용자 위치 기반 맛집 조회 목록", description = "사용자의 위치에서 주어진 범위(range) 만큼의 맛집 목록을 거리순(distance) or 평점순(rating)에 따라 정렬하여 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "맛집 조회 성공 (거리순 or 평점순)", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StoreListResponseDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "경도/위도/범위를 입력하지 않았을 때", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameter(name = "lon", description = "경도 (세로선=동서) (경도 범위 : -180 ~ 180)", example = "127.1108000763")
    @Parameter(name = "lat", description = "위도 (가로선=남북) (위도 범위 : -90 ~ 90)", example = "37.3503950812")
    @Parameter(name = "range", description = "경도, 위도로 지정한 위치 주변의 검색할 범위 값 (단위는 km 이며, range 1.0은 1km)", example = "1")
    @Parameter(name = "orderBy", description = "맛집 데이터 정렬 기준 2가지 : 거리순(distance,기본값) or 평점순(rating)", example = "distance")
    public ResponseEntity<StoreListResponseDto> getStoreList(@RequestParam(value = "lon") double lon,
                                                             @RequestParam(value = "lat") double lat,
                                                             @RequestParam(value = "range") double range,
                                                             @RequestParam(value = "orderBy", defaultValue = "distance", required = false) String orderBy) {
        StoreListResponseDto storeList = storeService.findStores(lon, lat, range, orderBy);
        if (storeList.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(storeList);
    }

    @GetMapping("/recommend")
    @Operation(summary = "사용자 위치 기반 맛집 추천 목록", description = "사용자의 위치에서 1km 반경의 맛집을 추천 (추천 최소 기준의 평균 : 평점 4.2 이상, 리뷰 개수 1000개 이상")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "맛집 추천 성공 (기본적으로 거리순으로 정렬)", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StoreListResponseDto.class)))
            }),
            @ApiResponse(responseCode = "400", description = "경도/위도를 입력하지 않았을 때", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<StoreListResponseDto> getRecommendStoreList(@Parameter(description = "경도 (세로선=동서) (경도 범위 : -180 ~ 180)", example = "127.1108000763") @RequestParam(value = "lon") double lon,
                                                                      @Parameter(description = "위도 (가로선=남북) (위도 범위 : -90 ~ 90)", example = "37.3503950812") @RequestParam(value = "lat") double lat) {
        double range = 1.0; // 사용자 위치 기반 맛집 추천이기에 검색 범위를 1km로 설정
        StoreListResponseDto storeList = storeService.findRecommendStores(lon, lat, range);
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
        if (category != null) {
            responseDto = storeService.findPopularCategoryStores(category);
        } else if (sigun != null) {
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
