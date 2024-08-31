package wanted.ribbon.genrestrt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
public class GenrestrtService {
    private final RestTemplate restTemplate;
    private final GenrestrtRepository genrestrtRepository;
    private final ObjectMapper objectMapper;

    private String baseUrl = "https://openapi.gg.go.kr";
    private String serviceKey = "4de6d887cc87485d8bbf242a03627fa4";

    // OpenAPI 서비스명
    public void fetchData() {
        fetchAndSaveData("/Genrestrtlunch"); // 김밥, 도시락
        fetchAndSaveData("/Genrestrtjpnfood"); // 일식
        fetchAndSaveData("/Genrestrtchifood"); // 중국집
    }
    /**
     * 주어진 API 응답과 서비스 이름에 따라 적절한 항목을 테이블에 저장합니다.
     * @param serviceName 서비스 이름 (엔드포인트 경로)
     */
    public void fetchAndSaveData(String serviceName) {
        int pageIndex = 1; // 페이지 위치
        int pageSize = 100; // 페이지 당 요청 숫자
        boolean hasMoreData = true;

        while (hasMoreData) {
            // 1. 응답 URL 생성
            String url = String.format("%s%s?KEY=%s&type=json&pIndex=%d&pSize=%d", baseUrl, serviceName, serviceKey, pageIndex, pageSize);

            try {
                // 2. API 호출
                String response = restTemplate.getForObject(url, String.class);
                if (response == null || response.isEmpty()) {
                    throw new GenrestrtException(ErrorCode.GENRESTRT_EMPTY_RESPONSE);
                }

                // 3. 응답
                if (response != null && !response.isEmpty()) {
                    // 4. 응답 파싱 객체 변환
                    GenrestrtApiResponse apiResponse = objectMapper.readValue(response, GenrestrtApiResponse.class);
                    // 5. row 데이터 추출
                    List<GenrestrtApiResponse.Row> items = extractItems(apiResponse, serviceName);
                    // 6. item이 비어있지 않으면 db 저장
                    if (items != null && !items.isEmpty()) {
                        saveDataToDatabase(items);
                    } else {
                        hasMoreData = false; // 데이터가 없으므로 반복 종료
                    }
                    // 7. 페이지 인덱스 증가
                    pageIndex++;
                } else {
                    hasMoreData = false; // 응답이 없으니 반복 종료
                }
            } catch (JsonProcessingException e) {
                throw new GenrestrtException(ErrorCode.GENRESTRT_PARSING_ERROR,e.getMessage());
            } catch (Exception e) {
                throw new GenrestrtException(ErrorCode.GENRESTRT_INTERNAL_SERVER_ERROR,e.getMessage());
            }
        }
    }

    /**
     * 주어진 API 응답과 서비스 이름에 따라 적절한 항목(row) 리스트를 추출
     * @param apiResponse API 응답 객체
     * @param serviceName 서비스 이름 (엔드포인트 경로)
     * @return 추출된 항목 리스트. 만약 항목이 없거나 서비스 이름이 잘못되었을 경우 빈 리스트를 반환
     */
    private List<GenrestrtApiResponse.Row> extractItems(GenrestrtApiResponse apiResponse, String serviceName) {
        // 1. 응답 기본값으로 빈 리스트 설정 (추후 null 체크 피하기)
        List<GenrestrtApiResponse.Row> rows = Collections.emptyList();

        // 2. 서비스 이름에 따라 적절한 항목을 추출
        switch (serviceName) {
            case "/Genrestrtlunch":
                // 2-1. `/Genrestrtlunch` 서비스에 해당하는 응답에서 모든 행을 추출
                rows = extractRows(apiResponse.getGenrestrtlunch());
                break;

            case "/Genrestrtjpnfood":
                // 2-2. `/Genrestrtjpnfood` 서비스에 해당하는 응답에서 모든 행을 추출
                rows = extractRows(apiResponse.getGenrestrtjpnfood());
                break;

            case "/Genrestrtchifood":
                // 2-3. `/Genrestrtchifood` 서비스에 해당하는 응답에서 모든 행을 추출
                rows = extractRows(apiResponse.getGenrestrtchifood());
                break;

            default:
                // 2-4. 알려지지 않은 서비스 이름이 주어졌을 경우
                throw new GenrestrtException(ErrorCode.GENRESTRT_UNKNOWN_SERVICE,
                        String.format("Unknown service name: %s", serviceName));
        }

        // 3. 추출된 행이 없음
        if (rows.isEmpty()) {
            throw new GenrestrtException(ErrorCode.GENRESTRT_NO_VALID_ROWS,
                    String.format("No valid rows found for service: %s", serviceName));
        }
        // 4. 최종적으로 추출된 항목 리스트를 반환
        return rows;
    }

    /**
     * 주어진 응답 리스트에서 모든 유효한 행을 추출합니다.
     * @param responses 응답 리스트 (Genrestrtchifood, Genrestrtjpnfood, Genrestrtlunch의 리스트)
     * @return 추출된 항목 리스트. 응답이 null인 경우 빈 리스트를 반환합니다.
     */
    private List<GenrestrtApiResponse.Row> extractRows(Object responses) {
        // 1. API 응답값이 null인 경우 빈 리스트를 반환합니다.
        if (responses == null) {
            return Collections.emptyList();
        }

        // 2-1. API 응답값이 List 타입인지 확인하고, 그렇다면 각 항목에서 행을 추출합니다.
        if (responses instanceof List<?>) {
            return ((List<?>) responses).stream()
                    // 2-2. 각 응답 객체에서 `getRow()` 메소드가 반환하는 리스트를 추출합니다.
                    .flatMap(response -> {
                        /*
                        * 응답 객체
                        * GenrestrtApiResponse$Genrestrtchifood (중국식),
                        * GenrestrtApiResponse$Genrestrtjpnfood (일식),
                        * GenrestrtApiResponse$Genrestrtlunch (김밥(도시락))
                        * */
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
                            // 2-3. 예상치 못한 타입의 객체가 들어온 경우 빈 스트림을 반환
                            return Stream.empty();
                        }
                    })
                    // 2-4. null이 아닌 유효한 행만 필터링
                    .filter(Objects::nonNull)
                    // 2-5. 최종적으로 필터링된 행들을 리스트로 수집
                    .collect(Collectors.toList());
        } else {
            // 2-6. 응답이 List 타입이 아닌 경우 빈 리스트를 반환
            return Collections.emptyList();
        }
    }

    // db 저장 메서드 (rawData)
    private void saveDataToDatabase(List<GenrestrtApiResponse.Row> items) {
        for (GenrestrtApiResponse.Row row : items) {
            Genrestrt genrestrt = mapToRawGenrestrt(row);
            genrestrtRepository.save(genrestrt);
        }
    }
    // 객체 변환
    private Genrestrt mapToRawGenrestrt(GenrestrtApiResponse.Row row) {
        return Genrestrt.builder()
                .sigunNm(row.getSigunNm())
                .sigunCd(row.getSigunCd())
                .bizplcNm(row.getBizplcNm())
                .licensgDe(parseDate(row.getLicensgDe()))
                .bsnStateNm(row.getBsnStateNm())
                .clsbizDe(parseDate(row.getClsbizDe()))
                .locplcAr(convertToDouble(row.getLocplcAr()))
                .gradFacltDivNm(row.getGradFacltDivNm())
                .maleEnflpsnCnt(convertToLong(row.getMaleEnflpsnCnt())) // integer -> long 타입 변환
                .yy(row.getYy())
                .multiUseBizestblYn(row.getMultiUseBizestblYn())
                .gradDivNm(row.getGradDivNm())
                .totFacltScale(convertToDouble(row.getTotFacltScale())) // string -> double 타입 변환
                .femaleEnflpsnCnt(convertToLong(row.getFemaleEnflpsnCnt())) // integer -> long 타입 변환
                .bsnsiteCircumfrDivNm(row.getBsnsiteCircumfrDivNm())
                .sanittnIndutypeNm(row.getSanittnIndutypeNm())
                .sanittnBizcondNm(row.getSanittnBizcondNm())
                .totEmplyCnt(convertToLong(row.getTotEmplyCnt()))  // integer -> long 타입 변환
                .refineRoadnmAddr(row.getRefineRoadnmAddr())
                .refineLotnoAddr(row.getRefineLotnoAddr())
                .refineZipCd(row.getRefineZipCd()) // String 유지
                .refineWgs84Lat(convertToDouble(row.getRefineWgs84Lat())) // string -> double 타입 변환
                .refineWgs84Logt(convertToDouble(row.getRefineWgs84Logt())) // string -> double 타입 변환
                .build();
    }

    // String -> Date 변환 메서드
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
    // String -> Integer 변환 메서드
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
    // String -> Double 변환 메서드
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
    // Integer -> Long 변환 메서드
    private Long convertToLong(Integer value) {
        if (value == null) {
            return null;
        }
        return value.longValue();
    }
}