package wanted.ribbon.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wanted.ribbon.store.domain.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.store.storeId = :storeId ORDER BY r.reviewId DESC")
    List<Review> findByStore_StoreId(Long storeId);
}
