package wanted.ribbon.genrestrt.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.ribbon.genrestrt.service.GenrestrtService;

@RestController
@RequestMapping("/api/genrestrts")
@RequiredArgsConstructor
public class GenrestrtController {
    private final GenrestrtService genrestrtService;

    // 경기도 맛집 데이터 수집 API
    @PostMapping("/fetch-data")
    public ResponseEntity<String> fetchData(@RequestParam("serviceName") String serviceName) {
        // openAPI 호출
        genrestrtService.fetchAndSaveData(serviceName);
        return ResponseEntity.ok(serviceName + "가 db에 성공적으로 저장됐습니다.");
    }

}
