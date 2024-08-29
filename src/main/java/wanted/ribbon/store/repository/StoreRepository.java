package wanted.ribbon.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.ribbon.store.domain.Store;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreId(Long StoreId);
}
