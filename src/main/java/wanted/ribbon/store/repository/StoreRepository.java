package wanted.ribbon.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.ribbon.store.domain.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
