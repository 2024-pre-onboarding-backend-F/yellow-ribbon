package wanted.ribbon.store.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom  {
    Optional<Store> findByStoreId(Long StoreId);

    @Query("SELECT s FROM Store s WHERE s.rating >= :rating AND s.reviewCount >= :reviewCount ORDER BY s.rating DESC, s.reviewCount DESC")
    List<Store> findPopularStores(@Param("rating")double rating, @Param("reviewCount")Integer reviewCount, Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.category = :category AND s.rating >= :rating AND s.reviewCount >= :reviewCount ORDER BY s.rating DESC, s.reviewCount DESC")
    List<Store> findPopularStoresByCategory(@Param("rating")double rating, @Param("reviewCount")Integer reviewCount, @Param("category") Category category, Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.sigun = :sigun AND s.rating >= :rating AND s.reviewCount >= :reviewCount ORDER BY s.rating DESC, s.reviewCount DESC")
    List<Store> findPopularStoresBySigun(@Param("rating")double rating, @Param("reviewCount")Integer reviewCount, @Param("sigun") String sigun, Pageable pageable);
}
