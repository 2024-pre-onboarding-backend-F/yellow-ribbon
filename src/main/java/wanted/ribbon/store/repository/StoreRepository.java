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

    // 거리순으로 정렬 (ST_Buffer와 ST_Contains로 인덱스 활용)
    @Query("SELECT s FROM Store s " +
            "WHERE ST_Contains(ST_Buffer(:userLocation, :meterRange), s.location) " +
            "ORDER BY ST_Distance(s.location, :userLocation) ASC") // 거리가 가까운 가게부터 정렬
    List<Store> findByLocationOrderByDistance(@Param("userLocation") Point userLocation, @Param("meterRange") double meterRange);

    // 평점순으로 정렬 (ST_Buffer와 ST_Contains로 인덱스 활용)
    @Query("SELECT s FROM Store s " +
            "WHERE ST_Contains(ST_Buffer(:userLocation, :meterRange), s.location) " +
            "ORDER BY s.rating DESC") // 평점이 높은 순서로 정렬
    List<Store> findByLocationOrderByRating(@Param("userLocation") Point userLocation, @Param("meterRange") double meterRange);
}
