package wanted.ribbon.store.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wanted.ribbon.store.domain.Review;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.store.storeId = :storeId ORDER BY r.reviewId DESC")
    List<Review> findByStore_StoreId(Long storeId);

    @Query("SELECT r.store, COUNT(r) as reviewCount FROM Review r " +
            "WHERE r.createdAt >= :startDate AND r.createdAt < :endDate " +
            "GROUP BY r.store " +
            "ORDER BY COUNT(r) DESC")
    List<Object[]> findStoresByReviewCount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
