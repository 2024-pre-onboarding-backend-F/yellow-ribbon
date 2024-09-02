package wanted.ribbon.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.ribbon.store.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {
}
