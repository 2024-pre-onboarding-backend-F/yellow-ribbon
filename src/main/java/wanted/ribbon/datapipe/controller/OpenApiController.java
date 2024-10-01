package wanted.ribbon.datapipe.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import wanted.ribbon.datapipe.dto.GyeongGiList;
import wanted.ribbon.datapipe.service.GenrestrtService;
import wanted.ribbon.datapipe.service.RawDataService;

@RestController
@RequestMapping("/api/datapipes")
@RequiredArgsConstructor
public class OpenApiController {
    private final GenrestrtService genrestrtService;
    private final RawDataService rawDataService;

    // 경기도 맛집 데이터 수집 API (RestTemplate), responsebody 사용으로 PostMapping으로 진행
    @PostMapping("/fetch-data")
    public ResponseEntity<String> fetchData(@RequestParam("serviceName") String serviceName) {
        // openAPI 호출
        genrestrtService.fetchAndSaveData(serviceName);
        return ResponseEntity.ok(serviceName + "가 db에 성공적으로 저장됐습니다.");
    }

    // 경기도 맛집 데이터 수집 API (WebClient, 모든 경기도 맛집 api 가능)
    /**
     * 데이터 조회로 GetMapping 사용
     * 비동기 처리를 위한 Mono 사용
     */
    @GetMapping("/fetch-and-save")
    public Mono<ResponseEntity<GyeongGiList>> fetchAndSaveData(@RequestParam("serviceName") String serviceName) {
        return rawDataService.getAndSaveByServiceName(serviceName)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
