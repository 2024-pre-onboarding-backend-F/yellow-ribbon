package wanted.ribbon.datapipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.ribbon.datapipe.domain.Genrestrt;

import java.util.List;
import java.util.Optional;

public interface GenrestrtRepository extends JpaRepository<Genrestrt, Long> {
    // 맛집 원본 데이터 중복 검사
    boolean existsByBizplcNmAndRefineRoadnmAddr(String bizplcNm, String refineRoadnmAddr);
    // 맛집 원본데이터 업장명, 도로명 주소로 조회
    Optional<Genrestrt> findByBizplcNmAndRefineRoadnmAddr(String bizplcNm, String refineRoadnmAddr);
    // 도로명 주소로 조회
    List<Genrestrt> findByRefineRoadnmAddrIn(List<String> refineRoadnmAddrs);
}
