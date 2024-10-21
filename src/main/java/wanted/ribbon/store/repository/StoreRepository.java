package wanted.ribbon.store.repository;

import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
    Optional<Store> findByStoreId(Long StoreId);

    @Query("SELECT s FROM Store s WHERE s.rating >= :rating AND s.reviewCount >= :reviewCount ORDER BY s.rating DESC, s.reviewCount DESC")
    List<Store> findPopularStores(@Param("rating")double rating, @Param("reviewCount")Integer reviewCount, Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.category = :category AND s.rating >= :rating AND s.reviewCount >= :reviewCount ORDER BY s.rating DESC, s.reviewCount DESC")
    List<Store> findPopularStoresByCategory(@Param("rating")double rating, @Param("reviewCount")Integer reviewCount, @Param("category") Category category, Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.sigun = :sigun AND s.rating >= :rating AND s.reviewCount >= :reviewCount ORDER BY s.rating DESC, s.reviewCount DESC")
    List<Store> findPopularStoresBySigun(@Param("rating")double rating, @Param("reviewCount")Integer reviewCount, @Param("sigun") String sigun, Pageable pageable);

    // 사용자 위치 기반 맛집 조회 목록 - 거리순으로 정렬
    @Query("SELECT s FROM Store s " +
            "WHERE ST_Contains(ST_Buffer(:userLocation, :meterRange), s.location) " + // ST_Buffer와 ST_Contains로 spatial index 활용
            "ORDER BY ST_Distance(s.location, :userLocation) ASC") // 거리가 가까운 가게부터 정렬
    List<Store> findByLocationOrderByDistance(@Param("userLocation") Point userLocation, @Param("meterRange") double meterRange);

    // 사용자 위치 기반 맛집 조회 목록 - 평점순으로 정렬
    @Query("SELECT s FROM Store s " +
            "WHERE ST_Contains(ST_Buffer(:userLocation, :meterRange), s.location) " +
            "ORDER BY s.rating DESC") // 평점이 높은 순서로 정렬
    List<Store> findByLocationOrderByRating(@Param("userLocation") Point userLocation, @Param("meterRange") double meterRange);

    // 사용자 위치 기반 맛집 추천 목록 - 평점과 리뷰 개수를 사용한 계산식이 3.0 이상인 맛집만 추천
    @Query("SELECT s FROM Store s " +
            "WHERE ST_Contains(ST_Buffer(:userLocation, 1000), s.location) " +  // 맛집 검색 범위 1000m(=1km로) 고정
            // 계산식 = 평점 * 0.7 + 리뷰 개수 가중치 계산 * 0.3
            "AND ((s.rating * 0.7) + (CASE " + 
            "    WHEN s.reviewCount > 3000 THEN 1.0 " +  // 리뷰 개수가 3000을 넘으면 1로 처리
            "    ELSE s.reviewCount / 3000.0 " +  // 3000 이하일 때는 정규화
            "END * 0.3)) >= 3.0 " +  // 계산식이 3.0 이상이면 추천
            "ORDER BY ST_Distance(s.location, :userLocation) ASC")  // 거리순으로 정렬
    List<Store> findRecommendStores(@Param("userLocation") Point userLocation);
}
