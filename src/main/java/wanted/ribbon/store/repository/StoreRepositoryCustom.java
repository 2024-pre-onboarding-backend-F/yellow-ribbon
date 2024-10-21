package wanted.ribbon.store.repository;

import org.locationtech.jts.geom.Point;
import wanted.ribbon.store.domain.Store;

import java.util.List;

public interface StoreRepositoryCustom {
    // 사용자 위치 기반 맛집 조회 목록
    List<Store> findAllStores(double lat, double lon, double meterToDegree, double meterRange, String orderBy);
    // 사용자 위치 기반 맛집 추천 목록
    List<Store> findAllRecommendStores(Point userLocation);
}
