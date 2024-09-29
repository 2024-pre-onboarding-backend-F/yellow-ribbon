package wanted.ribbon.datapipe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wanted.ribbon.datapipe.domain.Genrestrt;
import wanted.ribbon.datapipe.dto.GyeongGiList;
import wanted.ribbon.datapipe.repository.GenrestrtRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RawDataService {
    private final GenrestrtRepository genrestrtRepository;
    private final WebClient webClient;

    @Value("${spring.public-api.gyeong-gi-url}")
    private String baseUrl;
    @Value("${spring.public-api.service-key}")
    private String serviceKey;

    /**
     * 주어진 서비스 이름에 따라 데이터를 가져오고 데이터베이스에 저장합니다.
     * 이 메소드는 페이지네이션을 사용하여 모든 데이터를 가져옵니다.
     * 병렬처리로 성능 향상
     * @param serviceName API 서비스 이름
     * @return 처리된 데이터를 포함한 GyeongGiList의 Mono
     */
    public Mono<GyeongGiList> getAndSaveByServiceName(String serviceName) {
        return Mono.defer(() -> {
            // 페이지 인덱스를 위한 AtomicInteger 초기화
            AtomicInteger pageIndex = new AtomicInteger(1);
            // 한 페이지 당 가져올 데이터 수
            int pageSize = 100;
            // 응답 저장할 리스트
            List<GyeongGiList.GyeongGiApiResponse> allResponses = new ArrayList<>();

            return Mono.just(true)
                    // 1. 모든 페이지의 데이터를 순차적으로 가져오기
                    .expand(hasMore -> {
                        if (!hasMore) {
                            return Mono.empty(); // 더이상 데이터가 없으면 종료
                        }
                        return fetchAndProcessPage(serviceName, pageIndex.getAndIncrement(), pageSize)
                                .doOnNext(allResponses::addAll) // 응답 데이터 추가
                                .map(responses -> responses.size() == pageSize); // 다음 페이지 존재 여부 확인
                    })
                    // 2. true인 경우 계속 진행
                    .takeWhile(Boolean::booleanValue)
                    // 3. 데이터베이스 저장 및 서비스이름 전달
                    .then(Mono.defer(() -> {
                        GyeongGiList finalList = new GyeongGiList("모든 데이터가 성공적으로 처리되었습니다.", (long) allResponses.size(), allResponses);
                        return saveToDatabase(finalList,serviceName);
                    }))
                    .doOnSuccess(result -> log.info("데이터 처리 완료: {} 개의 항목 처리됨", result.total()))
                    .doOnError(error -> log.error("데이터 처리 중 오류 발생", error));
        });
    }

    /**
     * 맛집API 페이지의 데이터를 가져와 처리합니다.
     * @param serviceName API 서비스 이름
     * @param pageIndex 현재 페이지 인덱스
     * @param pageSize 페이지당 데이터 수
     * @return 처리된 응답 리스트의 Mono
     */
    private Mono<List<GyeongGiList.GyeongGiApiResponse>> fetchAndProcessPage(String serviceName, int pageIndex, int pageSize) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(baseUrl)
                        .path("/" + serviceName)
                        .queryParam("KEY", serviceKey)
                        .queryParam("Type", "json")
                        .queryParam("pIndex", pageIndex)
                        .queryParam("pSize", pageSize)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> parseStringResponse(responseBody, serviceName))
                .map(GyeongGiList::gyeongGiApiResponses)
                .doOnNext(responses -> log.info("페이지 {} 처리 완료: {} 개의 항목", pageIndex, responses.size()));
    }

    private GyeongGiList parseStringResponse(String responseBody, String serviceName) {
        log.info("API 응답: {}", responseBody);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseBody);
            return parseJsonResponse(jsonNode,serviceName);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 중 오류 발생: {}", e.getMessage());
            return new GyeongGiList("JSON 파싱 실패", 0L, Collections.emptyList());
        }
    }

    /**
     * JSON 응답을 파싱하여 GyeongGiList 객체로 변환합니다.
     * @param jsonNode 파싱할 JSON 노드
     * @return 파싱된 GyeongGiList 객체
     */
    private GyeongGiList parseJsonResponse(JsonNode jsonNode, String serviceName) {
        List<GyeongGiList.GyeongGiApiResponse> responses = new ArrayList<>();
        JsonNode serviceNode = jsonNode.path(serviceName);

        if (serviceNode.isMissingNode()) {
            log.warn("{} 노드를 찾을 수 없습니다.", serviceName);
            return new GyeongGiList("데이터 구조 오류", 0L, responses);
        }
        JsonNode rowsNode = serviceNode.path(1).path("row");
        if (rowsNode.isMissingNode() || !rowsNode.isArray()) {
            log.warn("row 배열을 찾을 수 없습니다.");
            return new GyeongGiList("데이터 구조 오류", 0L, responses);
        }
        for (JsonNode row : rowsNode) {
            // 각 행의 데이터를 GyeongGiApiResponse 객체로 변환
            try {
                GyeongGiList.GyeongGiApiResponse response = new GyeongGiList.GyeongGiApiResponse(
                        row.path("SIGUN_NM").asText(),
                        row.path("SIGUN_CD").asText(),
                        row.path("BIZPLC_NM").asText(),
                        parseDate(row.path("LICENSG_DE").asText()),
                        row.path("BSN_STATE_NM").asText(),
                        parseDate(row.path("CLSBIZ_DE").asText()),
                        row.path("LOCPLC_AR").asDouble(),
                        row.path("GRAD_FACLT_DIV_NM").asText(),
                        row.path("MALE_ENFLPSN_CNT").asLong(),
                        row.path("YY").asInt(),
                        row.path("MULTI_USE_BIZESTBL_YN").asText(),
                        row.path("GRAD_DIV_NM").asText(),
                        row.path("TOT_FACLT_SCALE").asDouble(),
                        row.path("FEMALE_ENFLPSN_CNT").asLong(),
                        row.path("BSNSITE_CIRCUMFR_DIV_NM").asText(),
                        row.path("SANITTN_INDUTYPE_NM").asText(),
                        row.path("SANITTN_BIZCOND_NM").asText(),
                        row.path("TOT_EMPLY_CNT").asLong(),
                        row.path("REFINE_LOTNO_ADDR").asText(),
                        row.path("REFINE_ROADNM_ADDR").asText(),
                        row.path("REFINE_ZIP_CD").asText(),
                        row.path("REFINE_WGS84_LOGT").asDouble(),
                        row.path("REFINE_WGS84_LAT").asDouble()
                );
                responses.add(response);
            } catch (Exception e) {
                log.error("행 파싱 중 오류 발생: {}", e.getMessage());
            }
        }
        return new GyeongGiList("성공적으로 데이터를 파싱했습니다.", (long) responses.size(), responses);
    }

    /**
     * 데이터를 비동기적으로 데이터베이스에 저장합니다.
     * @param gyeongGiList 저장할 GyeongGiList 객체
     * @return 저장된 GyeongGiList의 Mono
     */
    private Mono<GyeongGiList> saveToDatabase(GyeongGiList gyeongGiList, String serviceName) {
        return Mono.fromCallable(() -> {
            List<Genrestrt> genrestrts = gyeongGiList.gyeongGiApiResponses().stream()
                    .map(this::convertToGenrestrt)
                    .collect(Collectors.toList());
            List<Genrestrt> savedGenrestrts = genrestrtRepository.saveAll(genrestrts);
            int savedCount = savedGenrestrts.size();
            String message = serviceName + "의 원본데이터 " + savedCount + "개가 DB에 성공적으로 저장됐습니다.";
            return new GyeongGiList(message, (long) savedCount, gyeongGiList.gyeongGiApiResponses());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * GyeongGiApiResponse 객체를 Genrestrt 엔티티로 변환합니다.
     * @param response 변환할 GyeongGiApiResponse 객체
     * @return 변환된 Genrestrt 엔티티
     */
    private Genrestrt convertToGenrestrt(GyeongGiList.GyeongGiApiResponse response) {
        return Genrestrt.builder()
                .sigunNm(response.sigunNm())
                .sigunCd(response.sigunCd())
                .bizplcNm(response.bizplcNm())
                .licensgDe(response.licensgDe())
                .bsnStateNm(response.bsnStateNm())
                .clsbizDe(response.clsbizDe())
                .locplcAr(response.locplcAr())
                .gradFacltDivNm(response.gradFacltDivNm())
                .maleEnflpsnCnt(response.maleEnflpsnCnt())
                .yy(response.yy())
                .multiUseBizestblYn(response.multiUseBizestblYn())
                .gradDivNm(response.gradDivNm())
                .totFacltScale(response.totFacltScale())
                .femaleEnflpsnCnt(response.femaleEnflpsnCnt())
                .bsnsiteCircumfrDivNm(response.bsnsiteCircumfrDivNm())
                .sanittnIndutypeNm(response.sanittnIndutypeNm())
                .sanittnBizcondNm(response.sanittnBizcondNm())
                .totEmplyCnt(response.totEmplyCnt())
                .refineRoadnmAddr(response.refineRoadnmAddr())
                .refineLotnoAddr(response.refineLotnoAddr())
                .refineZipCd(response.refineZipCd())
                .refineWgs84Lat(response.refineWgs84Lat())
                .refineWgs84Logt(response.refineWgs84Logt())
                .build();
    }

    /**
     * 문자열을 Date 객체로 파싱합니다.
     * @param dateString 파싱할 날짜 문자열
     * @return 파싱된 Date 객체, 파싱 실패 시 null
     */
    private Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            log.error("날짜 파싱 오류: {}", dateString, e);
            return null;
        }
    }
}