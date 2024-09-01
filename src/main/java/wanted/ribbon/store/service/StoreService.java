package wanted.ribbon.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wanted.ribbon.store.domain.Store;
import wanted.ribbon.store.dto.StoreResponseDto;
import wanted.ribbon.store.repository.StoreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    /**
     * 위도, 경도에서 첫 번째 소수점 자리는 최대 11.1km, 두 번째 소수점 자리는 1.1 km, 세 번째 소수점 자리는 110m 이다.
     *
     * @param lat     위도
     * @param lon     경도
     * @param range   위도, 경도로 지정한 위치 주변의 검색할 범위 설정 값 (단위는 km이며, range 1.0은 1km이다.)
     * @param orderBy store 데이터 정렬 기준 (거리순과 평점순 2가지)
     */
    public List<StoreResponseDto> findStores(double lat, double lon, double range, String orderBy) {
        // 위도, 경도의 계산을 위해 km를 m로 변환
        double meterRange = range * 1000;
        // bbox를 구하는 4 모서리 좌표 계산에 활용하기 위해 반으로 나눔
        double moveRange = meterRange / 2;
        // 위도, 경도에서 0.01도는 1100m인 것을 사용해 몇 m는 위도, 경도로 어느 정도인지 계산
        double meterToDegree = moveRange * 0.01 / 1100; // 0.01 : 1100 = meterToDegree : moveRange(몇 m)

        List<Store> storeList = storeRepository.findAllStores(lat, lon, meterToDegree, meterRange, orderBy);
        List<StoreResponseDto> storeListDto = storeList.stream().map(StoreResponseDto::from).collect(Collectors.toList());
        return storeListDto;
    }
}
