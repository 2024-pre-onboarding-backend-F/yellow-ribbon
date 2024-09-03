package wanted.ribbon.genrestrt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wanted.ribbon.exception.ErrorCode;
import wanted.ribbon.exception.GenrestrtException;
import wanted.ribbon.genrestrt.domain.Genrestrt;
import wanted.ribbon.genrestrt.dto.GenrestrtApiResponse;
import wanted.ribbon.genrestrt.repository.GenrestrtRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GenrestrtService {
    private final RestTemplate restTemplate;
    private final GenrestrtRepository genrestrtRepository;
    private final ObjectMapper objectMapper;

    @Value("${spring.public-api.base-url}")
    private String baseUrl;
    @Value("${spring.public-api.service-key}")
    private String serviceKey;

    // 데이터 가져오기를 위한 메인 메서드
    public void fetchData() {
        // 다양한 서비스명을 이용해 데이터를 가져옵니다.
        fetchAndSaveData("/Genrestrtlunch"); // 김밥, 도시락
        fetchAndSaveData("/Genrestrtjpnfood"); // 일식
        fetchAndSaveData("/Genrestrtchifood"); // 중국집
    }

    /**
     * 주어진 서비스 이름에 따라 데이터를 가져오고 데이터베이스에 저장합니다.
     * @param serviceName 서비스 이름 (API 엔드포인트 경로)
     */
    public void fetchAndSaveData(String serviceName) {
        int pageIndex = 1; // 페이지 인덱스 초기값 설정
        int pageSize = 100; // 페이지 당 요청할 데이터 수
        boolean hasMoreData = true; // 데이터가 더 있는지 여부
        List<GenrestrtApiResponse.Row> allItems = new ArrayList<>(); // 모든 데이터를 저장할 리스트

        while (hasMoreData) {
            // 1. API 요청 URL 생성
            String url = String.format("%s%s?KEY=%s&type=json&pIndex=%d&pSize=%d", baseUrl, serviceName, serviceKey, pageIndex, pageSize);
            // 로그: 요청 URL 기록
            log.info("Fetching data from URL: {}", url);
            try {
                // 2. API 호출
                String response = restTemplate.getForObject(url, String.class);

                if (response == null || response.isEmpty()) {
                    throw new GenrestrtException(ErrorCode.GENRESTRT_EMPTY_RESPONSE, "Empty response received.");
                }

                // 3. openApi 응답을 API 응답 객체로 변환
                GenrestrtApiResponse apiResponse = objectMapper.readValue(response, GenrestrtApiResponse.class);

                // 4. 응답에서 데이터 항목 추출
                List<GenrestrtApiResponse.Row> items = extractItems(apiResponse, serviceName);

                // 5-1. 데이터가 있으면 데이터베이스에 저장 -> 페이지 인덱스를 증가
                if (!items.isEmpty()) {
                    allItems.addAll(items);
                    pageIndex++;
                } else {
                    // 5-2.데이터가 없으면 반복 종료
                    hasMoreData = false;
                    log.info(url,"더 이상 데이터가 없습니다.");
                }
            } catch (JsonProcessingException e) {
                // JSON 파싱 오류 발생 시 로그 기록
                log.error("JSON parsing error for URL: {}", url, e);
                break;
            } catch (Exception e) {
                // 기타 오류 발생 시 예외 처리
                log.error("Internal server error for URL: {}", url, e);
                break;
            }
        }
        // 6. 모든 데이터를 db에 저장
        if (!allItems.isEmpty()) {
            saveDataToDatabase(allItems);
            log.info(serviceName,"의 openApi 데이터가 성공적으로 저장되었습니다.");
        } else {
            log.info("db에 저장된 값이 없습니다.");
        }
    }

    /**
     * 주어진 API 응답 및 서비스 이름에 따라 적절한 데이터 항목을 추출합니다.
     * @param apiResponse API 응답 객체
     * @param serviceName 서비스 이름 (API 엔드포인트 경로)
     * @return 추출된 항목 리스트
     */
    private List<GenrestrtApiResponse.Row> extractItems(GenrestrtApiResponse apiResponse, String serviceName) {
        // 1. 기본값으로 빈 리스트를 설정
        List<GenrestrtApiResponse.Row> rows = Collections.emptyList();

        // 2. 서비스 이름에 따라 적절한 항목 추출
        switch (serviceName) {
            case "/Genrestrtlunch":
                rows = extractRows(apiResponse.getGenrestrtlunch());
                break;
            case "/Genrestrtjpnfood":
                rows = extractRows(apiResponse.getGenrestrtjpnfood());
                break;
            case "/Genrestrtchifood":
                rows = extractRows(apiResponse.getGenrestrtchifood());
                break;
            default:
                // 알려지지 않은 서비스 이름에 대한 예외 처리
                throw new GenrestrtException(ErrorCode.GENRESTRT_UNKNOWN_SERVICE, String.format("Unknown service name: %s", serviceName));
        }

        // 3. 유효한 데이터가 없는 경우 예외 처리
        if (rows.isEmpty()) {
            throw new GenrestrtException(ErrorCode.GENRESTRT_NO_VALID_ROWS, String.format("No valid rows found for service: %s", serviceName));
        }
        // 4. 최종적으로 추출된 리스트 반환
        return rows;
    }

    /**
     * 응답 리스트에서 모든 유효한 데이터 항목을 추출합니다.
     * @param responses 응답 리스트 (각 서비스에 대한 데이터)
     * @return 유효한 항목 리스트
     */
    private List<GenrestrtApiResponse.Row> extractRows(Object responses) {
        // 1. API 응답값이 null인 경우 빈 리스트 반환
        if (responses == null) {
            return Collections.emptyList();
        }
        // 2-1. API 응답값이 List 타입인지 확인하고, 그렇다면 각 항목에서 행을 추출합니다.
        if (responses instanceof List<?>) {
            return ((List<?>) responses).stream()
                    .flatMap(response -> {
                        // 2-2. 응답 객체에 따라 데이터 항목을 추출
                        if (response instanceof GenrestrtApiResponse.Genrestrtchifood) {
                            return Optional.ofNullable(((GenrestrtApiResponse.Genrestrtchifood) response).getRow())
                                    .orElse(Collections.emptyList())
                                    .stream();
                        } else if (response instanceof GenrestrtApiResponse.Genrestrtjpnfood) {
                            return Optional.ofNullable(((GenrestrtApiResponse.Genrestrtjpnfood) response).getRow())
                                    .orElse(Collections.emptyList())
                                    .stream();
                        } else if (response instanceof GenrestrtApiResponse.Genrestrtlunch) {
                            return Optional.ofNullable(((GenrestrtApiResponse.Genrestrtlunch) response).getRow())
                                    .orElse(Collections.emptyList())
                                    .stream();
                        } else {
                            return Stream.empty(); // 예상치 못한 타입일 경우 빈 스트림 반환
                        }
                    })
                    .filter(Objects::nonNull) // null 값 필터링
                    .collect(Collectors.toList()); // 리스트로 수집
        } else {
            return Collections.emptyList(); // 리스트가 아닌 경우 빈 리스트 반환
        }
    }

    /**
     * 추출된 데이터를 데이터베이스에 저장합니다.
     * @param items 저장할 데이터 항목 리스트
     * 데이터 배치 저장 방식 활용
     */
    private void saveDataToDatabase(List<GenrestrtApiResponse.Row> items) {
        List<Genrestrt> entities = items.stream()
                .map(this::mapToRawGenrestrt) // 각 항목을 엔티티로 변환
                .collect(Collectors.toList()); // 리스트로 수집
        genrestrtRepository.saveAll(entities); // 데이터베이스에 저장
    }

    /**
     * API 응답 항목을 데이터베이스 엔티티로 변환합니다.
     * @param row API 응답 항목
     * @return 변환된 데이터베이스 엔티티
     */
    private Genrestrt mapToRawGenrestrt(GenrestrtApiResponse.Row row) {
        return Genrestrt.builder()
                .sigunNm(row.getSigunNm())
                .sigunCd(row.getSigunCd())
                .bizplcNm(row.getBizplcNm())
                .licensgDe(parseDate(row.getLicensgDe())) // 문자열 날짜를 Date 객체로 변환
                .bsnStateNm(row.getBsnStateNm())
                .clsbizDe(parseDate(row.getClsbizDe())) // 문자열 날짜를 Date 객체로 변환
                .locplcAr(convertToDouble(row.getLocplcAr())) // 문자열을 Double로 변환
                .gradFacltDivNm(row.getGradFacltDivNm())
                .maleEnflpsnCnt(convertToLong(row.getMaleEnflpsnCnt())) // Integer를 Long으로 변환
                .yy(row.getYy())
                .multiUseBizestblYn(row.getMultiUseBizestblYn())
                .gradDivNm(row.getGradDivNm())
                .totFacltScale(convertToDouble(row.getTotFacltScale())) // 문자열을 Double로 변환
                .femaleEnflpsnCnt(convertToLong(row.getFemaleEnflpsnCnt())) // Integer를 Long으로 변환
                .bsnsiteCircumfrDivNm(row.getBsnsiteCircumfrDivNm())
                .sanittnIndutypeNm(row.getSanittnIndutypeNm())
                .sanittnBizcondNm(row.getSanittnBizcondNm())
                .totEmplyCnt(convertToLong(row.getTotEmplyCnt())) // Integer를 Long으로 변환
                .refineRoadnmAddr(row.getRefineRoadnmAddr())
                .refineLotnoAddr(row.getRefineLotnoAddr())
                .refineZipCd(row.getRefineZipCd()) // 문자열 그대로 유지
                .refineWgs84Lat(convertToDouble(row.getRefineWgs84Lat())) // 문자열을 Double로 변환
                .refineWgs84Logt(convertToDouble(row.getRefineWgs84Logt())) // 문자열을 Double로 변환
                .processed(false) // 초기 데이터 처리 상태 false
                .build();
    }

    /**
     * 문자열을 Date 객체로 변환합니다.
     * @param dateStr 문자열 날짜
     * @return 변환된 Date 객체
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 문자열을 Integer로 변환합니다.
     * @param value 문자열 값
     * @return 변환된 Integer 값
     */
    private Integer convertToInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 문자열을 Double로 변환합니다.
     * @param value 문자열 값
     * @return 변환된 Double 값
     */
    private Double convertToDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Integer를 Long으로 변환합니다.
     * @param value Integer 값
     * @return 변환된 Long 값
     */
    private Long convertToLong(Integer value) {
        if (value == null) {
            return null;
        }
        return value.longValue();
    }
}
