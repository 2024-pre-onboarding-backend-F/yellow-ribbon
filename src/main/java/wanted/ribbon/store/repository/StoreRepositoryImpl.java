package wanted.ribbon.store.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wanted.ribbon.store.domain.Store;

import java.util.List;

import static wanted.ribbon.store.domain.QStore.store;

@Component
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Store> findAllStores(double lat, double lon, double meterToDegree, double meterRange, String orderBy) {
        double minLat = lat - meterToDegree;
        double maxLat = lat + meterToDegree;
        double minLon = lon - meterToDegree;
        double maxLon = lon + meterToDegree;

        // orderBy는 거리순(distance), 평점순(rating) 2가지이며 default는 거리순이다.
        return queryFactory.selectFrom(store)
                .where(store.storeLat.between(minLat, maxLat),
                        store.storeLon.between(minLon, maxLon),
                        calculateDistance(lat, lon, store.storeLat, store.storeLon).loe(meterRange))
                .orderBy(getOrderSpecifier(orderBy, lat, lon))
                .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String orderBy, double lat, double lon) {
        if ("rating".equalsIgnoreCase(orderBy)) {
            return store.rating.desc();  // 평점순 정렬
        } else {
            // 기본값으로 거리순 정렬
            NumberExpression<Double> distance = calculateDistance(lat, lon, store.storeLat, store.storeLon);
            return distance.asc();
        }
    }

    private NumberExpression<Double> calculateDistance(double lat, double lon,
                                                       NumberPath<Double> storeLat,
                                                       NumberPath<Double> storeLon) {
        // 아래의 식은 두 지점 간의 거리를 계산하는 하버사인(Haversine) 공식의 변형된 버전이며, m의 값을 반환한다.
        // 하버사인 공식은 구면 기하학을 사용하여 지구 표면상의 두 점 사이의 거리를 계산한다.
        return Expressions.numberTemplate(Double.class,
                "(1000 * 6371 * acos(cos(radians({0})) * cos(radians({2})) * cos(radians({3}) - radians({1})) + sin(radians({0})) * sin(radians({2}))))",
                lat, lon, storeLat, storeLon);
    }
}
