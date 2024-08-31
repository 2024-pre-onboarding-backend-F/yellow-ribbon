package wanted.ribbon.genrestrt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wanted.ribbon.genrestrt.domain.Genrestrt;

@Repository
public interface GenrestrtRepository extends JpaRepository<Genrestrt, Long> {
}
