package wanted.ribbon.store.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import wanted.ribbon.store.domain.Store;

import java.util.List;

import static wanted.ribbon.store.domain.QStore.store;

@Component
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private static final int MAX_REVIEW_COUNT = 3000;

    // 사용자 위치 기반 맛집 추천 목록
    @Override
    public List<Store> findAllRecommendStores(Point userLocation) {
        // wherw 절 설정 = 사용자 위치 1km 반경 + 리뷰 개수로 계산한 식의 최소 기준 이상으로 필터링
        BooleanBuilder whereClause = new BooleanBuilder();

        // wherw 절 조건 1 - 사용자 위치 1km 범위 내의 가게 필터링
        whereClause.and(
                Expressions.booleanTemplate(
                        "ST_Contains(ST_Buffer({0}, 1000), {1})", userLocation, store.location // 맛집 검색 범위 1000m(=1km로) 고정
                )
        );

        // CASE 문으로 리뷰 개수 정규화
        NumberExpression<Double> normalizedReviewCount = Expressions.cases()
                .when(store.reviewCount.gt(MAX_REVIEW_COUNT)).then(1.0) // 리뷰 개수가 3000을 넘으면 1로 처리
                .otherwise(store.reviewCount.doubleValue().divide(MAX_REVIEW_COUNT)); // 3000 이하일 때는 정규화

        // 평점과 리뷰 개수를 이용한 최종 점수 계산
        NumberExpression<Double> score = store.rating.multiply(0.7).add(normalizedReviewCount.multiply(0.3));

        // where 절 조건 2 - 평점과 리뷰 개수로 계산한 score가 3.0 이상인 경우만 필터링
        whereClause.and(score.goe(3.0));

        return queryFactory
                .selectFrom(store)
                .where(whereClause)
                .orderBy( // 거리순으로 정렬
                        Expressions.numberTemplate(Double.class, "ST_Distance({0}, {1})", store.location, userLocation).asc()
                )
                .fetch();
    }

    // 사용자 위치 기반 맛집 조회 목록
    @Override
    public List<Store> findAllStores(double lat, double lon, double meterToDegree, double meterRange, String orderBy) {
        double minLat = lat - meterToDegree;
        double maxLat = lat + meterToDegree;
        double minLon = lon - meterToDegree;
        double maxLon = lon + meterToDegree;

        return queryFactory.selectFrom(store)
                .where(store.storeLat.between(minLat, maxLat),
                        store.storeLon.between(minLon, maxLon),
                        calculateDistance(lat, lon, store.storeLat, store.storeLon).loe(meterRange))
                .orderBy(getOrderSpecifier(orderBy, lat, lon))
                .fetch();
    }

    // orderBy는 거리순(distance), 평점순(rating) 2가지이며 default는 거리순이다.
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
