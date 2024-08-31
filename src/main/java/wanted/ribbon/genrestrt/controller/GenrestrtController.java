package wanted.ribbon.genrestrt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import wanted.ribbon.genrestrt.service.GenrestrtService;

@RestController
@RequestMapping("/api/genrestrts")
@RequiredArgsConstructor
public class GenrestrtController {
    private final GenrestrtService genrestrtService;

    // 경기도 맛집 데이터 수집 API
    @GetMapping("/fetch-data")
    public ResponseEntity<String> fetchData(@RequestParam("serviceName") String serviceName) {
        // openAPI 호출
        genrestrtService.fetchAndSaveData(serviceName);
        return ResponseEntity.ok(serviceName + "가 db에 성공적으로 저장됐습니다.");
    }

}
