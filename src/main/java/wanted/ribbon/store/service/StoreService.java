package wanted.ribbon.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.ribbon.exception.ErrorCode;
import wanted.ribbon.exception.NotFoundException;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Review;
import wanted.ribbon.store.domain.Store;
import wanted.ribbon.store.dto.*;
import wanted.ribbon.store.repository.ReviewRepository;
import wanted.ribbon.store.repository.StoreRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    public StoreDetailResponseDto getStoreDetail(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));
        List<Review> reviewList = reviewRepository.findByStore_StoreId(storeId);
        List<ReviewListResponseDto> reviewListResponseDto = reviewList.stream()
                .map(list -> new ReviewListResponseDto(list.getUser().getId(), list.getScore(), list.getContent()))
                .collect(Collectors.toList());

        StoreDetailResponseDto responseDto = new StoreDetailResponseDto(
                storeId,
                store.getSigun(),
                store.getStoreName(),
                store.getCategory(),
                store.getAddress(),
                store.getStoreLat(),
                store.getStoreLon(),
                store.getRating(),
                reviewListResponseDto
        );
        return responseDto;
    }

    /**
     * 위도, 경도에서 첫 번째 소수점 자리는 최대 11.1km, 두 번째 소수점 자리는 1.1 km, 세 번째 소수점 자리는 110m 이다.
     *
     * @param lat     위도
     * @param lon     경도
     * @param range   위도, 경도로 지정한 위치 주변의 검색할 범위 설정 값 (단위는 km이며, range 1.0은 1km이다.)
     * @param orderBy store 데이터 정렬 기준 (거리순과 평점순 2가지)
     */
    public StoreListResponseDto findStores(double lat, double lon, double range, String orderBy) {
        // 위도, 경도의 계산을 위해 km를 m로 변환
        double meterRange = range * 1000;
        // bbox를 구하는 4 모서리 좌표 계산에 활용하기 위해 반으로 나눔
        double moveRange = meterRange / 2;
        // 위도, 경도에서 0.01도는 1100m인 것을 사용해 몇 m는 위도, 경도로 어느 정도인지 계산
        double meterToDegree = moveRange * 0.01 / 1100; // 0.01 : 1100 = meterToDegree : moveRange(몇 m)

        List<Store> storeList = storeRepository.findAllStores(lat, lon, meterToDegree, meterRange, orderBy);
        return StoreListResponseDto.fromStoreList(storeList);
    }

    @Transactional
    @Cacheable(value = "popularstores", key = "'popular'", cacheManager = "cacheManager")
    public PopularStoreListResponseDto popularStores() {
        Pageable pageable = PageRequest.of(0, 100);
        List<Store> storeList = storeRepository.findPopularStores(4.5, 100, pageable);
        return PopularStoreListResponseDto.fromStoreList(storeList);
    }

    @Transactional
    @Cacheable(value = "popularstores", key = "#category.name()", cacheManager = "cacheManager")
    public PopularStoreListResponseDto findPopularCategoryStores(Category category) {
        Pageable pageable = PageRequest.of(0, 100);
        List<Store> storeList = storeRepository.findPopularStoresByCategory(4.5, 100, category, pageable);
        return PopularStoreListResponseDto.fromStoreList(storeList);
    }

    @Transactional
    @Cacheable(value = "popularstores", key = "#sigun", cacheManager = "cacheManager")
    public PopularStoreListResponseDto findPopularSigunStores(String sigun) {
        Pageable pageable = PageRequest.of(0, 100);
        List<Store> storeList = storeRepository.findPopularStoresBySigun(4.5, 100, sigun, pageable);
        return PopularStoreListResponseDto.fromStoreList(storeList);
    }

    @Transactional
    @Cacheable(value = "risingpopularstores", key = "'rising'", cacheManager = "cacheManager")
    public RisingPopularStoreListResponseDto findRisingStores() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startDate = yesterday.atStartOfDay();
        LocalDateTime endDate = LocalDate.now().atStartOfDay();
        List<Object[]> storeList = reviewRepository.findStoresByReviewCount(startDate, endDate, pageable);
        // Object[]를 Store로 변환
        List<Store> risingStoreList = storeList.stream()
                .map(result -> (Store) result[0])
                .collect(Collectors.toList());
        return RisingPopularStoreListResponseDto.fromRisingStoreList(risingStoreList);
    }
}
