package wanted.ribbon.datapipe.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="DataPipe",description = "경기도 공공데이터 수집 관련 API입니다.")
public class OpenApiController {
    private final GenrestrtService genrestrtService;
    private final RawDataService rawDataService;

    // 경기도 맛집 데이터 수집 API (RestTemplate), responsebody 사용으로 PostMapping으로 진행
    @PostMapping("/fetch-data")
    @Operation(summary = "경기도 맛집 수집", description = "경기도 맛집 데이터를 restTemplate 방식으로 가져옵니다. " +
            "김밥, 중국식, 일식 등 일부 카테고리만 가져올 수 있습니다.")
    public ResponseEntity<String> fetchData(@RequestParam("serviceName") String serviceName) {
        // openAPI 호출
        genrestrtService.fetchAndSaveData(serviceName);
        return ResponseEntity.ok(serviceName + "가 db에 성공적으로 저장됐습니다.");
    }

    /**
     * 데이터 조회로 GetMapping 사용
     * 비동기 처리를 위한 Mono 사용
     */
    @GetMapping("/fetch-and-save")
    @Operation(summary = "경기도 맛집 수집", description = "경기도 맛집 데이터를 webClient 방식으로 가져옵니다. " +
            "경기도 맛집의 모든 음식점 카테고리를 가져올 수 있습니다.")
    public Mono<ResponseEntity<GyeongGiList>> fetchAndSaveData(@RequestParam("serviceName") String serviceName) {
        return rawDataService.getAndSaveByServiceName(serviceName)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
