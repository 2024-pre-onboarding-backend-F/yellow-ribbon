package wanted.ribbon.datapipe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import wanted.ribbon.datapipe.domain.Genrestrt;
import wanted.ribbon.datapipe.dto.GyeongGiList;
import wanted.ribbon.datapipe.repository.GenrestrtRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RawDataSaveService {
    private final DataSource dataSource; // jdbc batch
    private final GenrestrtRepository genrestrtRepository;
    private static final int BATCH_SIZE = 1000;

    /**
     * 데이터를 데이터베이스에 저장합니다.
     * @param gyeongGiList 저장할 GyeongGiList 객체
     * @return 저장된 GyeongGiList의 Mono
     */
    @Transactional
    public Mono<GyeongGiList> saveToDatabase(GyeongGiList gyeongGiList, String serviceName) {
        return Mono.fromCallable(() -> {
            Map<String, Genrestrt> existingGenrestrts = loadExistingGenrestrts(gyeongGiList);
            List<GyeongGiList.GyeongGiApiResponse> newResponses = new ArrayList<>();
            List<Genrestrt> toUpdate = new ArrayList<>();
            int unchangedCount = 0;

            for (GyeongGiList.GyeongGiApiResponse response : gyeongGiList.gyeongGiApiResponses()) {
                String key = response.bizplcNm() + "|" + response.refineRoadnmAddr();
                if (existingGenrestrts.containsKey(key)) {
                    Genrestrt existingGenrestrt = existingGenrestrts.get(key);
                    if (hasChanged(existingGenrestrt, response)) {
                        updateExistingGenrestrt(existingGenrestrt, response);
                        toUpdate.add(existingGenrestrt);
                    } else {
                        unchangedCount++;
                    }
                } else {
                    newResponses.add(response);
                }
            }

            // JPA를 사용한 업데이트
            genrestrtRepository.saveAll(toUpdate);
            int updatedCount = toUpdate.size();

            // JDBC를 사용한 새 데이터 삽입
            int insertedCount = batchInsertNewData(newResponses);

            String message = String.format("%s의 원본데이터 중 %d개가 새로 저장되고, %d개가 업데이트 됐으며, %d개는 변경사항 없습니다.",
                    serviceName, insertedCount, updatedCount, unchangedCount);
            return new GyeongGiList(message, (long) (insertedCount + updatedCount + unchangedCount), insertedCount,updatedCount,unchangedCount,gyeongGiList.gyeongGiApiResponses());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 새로운 데이터를 jdbc batch를 활용해 저장합니다.
     */
    private int batchInsertNewData(List<GyeongGiList.GyeongGiApiResponse> newResponses) {
        // 중복값 insert 무시 > ignore 사용
        String sql = "INSERT IGNORE INTO genrestrts (sigun_nm, sigun_cd, bizplc_nm, licensg_de, bsn_state_nm, clsbiz_de," +
                "locplc_ar, grad_faclt_div_nm, male_enflpsn_cnt, yy, multi_use_bizestbl_yn, grad_div_nm," +
                "tot_faclt_scale, female_enflpsn_cnt, bsnsite_circumfr_div_nm,sanittn_indutype_nm, sanittn_bizcond_nm, " +
                "tot_emply_cnt, refine_roadnm_addr, refine_lotno_addr,refine_zip_cd,refine_wgs84lat,refine_wgs84logt,processed) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,false)";

        int insertedCount = 0;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (int i = 0; i < newResponses.size(); i++) {
                GyeongGiList.GyeongGiApiResponse response = newResponses.get(i);
                pstmt.setString(1, response.sigunNm());
                pstmt.setString(2, response.sigunCd());
                pstmt.setString(3, response.bizplcNm());
                pstmt.setDate(4, convertToSqlDate(response.licensgDe()));
                pstmt.setString(5, response.bsnStateNm());
                pstmt.setDate(6, convertToSqlDate(response.clsbizDe()));
                pstmt.setDouble(7, response.locplcAr());
                pstmt.setString(8, response.gradFacltDivNm());
                pstmt.setLong(9, response.maleEnflpsnCnt());
                pstmt.setInt(10, response.yy());
                pstmt.setString(11, response.multiUseBizestblYn());
                pstmt.setString(12, response.gradDivNm());
                pstmt.setDouble(13, response.totFacltScale());
                pstmt.setLong(14, response.femaleEnflpsnCnt());
                pstmt.setString(15, response.bsnsiteCircumfrDivNm());
                pstmt.setString(16, response.sanittnIndutypeNm());
                pstmt.setString(17, response.sanittnBizcondNm());
                pstmt.setLong(18, response.totEmplyCnt());
                pstmt.setString(19, response.refineRoadnmAddr());
                pstmt.setString(20, response.refineLotnoAddr());
                pstmt.setString(21, response.refineZipCd());
                pstmt.setDouble(22, response.refineWgs84Lat());
                pstmt.setDouble(23, response.refineWgs84Logt());
                pstmt.addBatch();

                if ((i + 1) % BATCH_SIZE == 0) {
                    insertedCount += executeBatch(pstmt, conn);
                }
            }

            insertedCount += executeBatch(pstmt, conn);

        } catch (SQLException e) {
            log.error("배치 삽입 중 오류 발생", e);
        }

        return insertedCount;
    }

    private int executeBatch(PreparedStatement pstmt, Connection conn) throws SQLException {
        int[] results = pstmt.executeBatch();
        conn.commit();
        return Arrays.stream(results).sum();
    }

    // sql 방식으로 date 변환 메서드
    private java.sql.Date convertToSqlDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()) : null;
    }

    // 중복키로 엔티티 상태 관리
    private Map<String, Genrestrt> loadExistingGenrestrts(GyeongGiList gyeongGiList) {
        // API 응답에서 모든 도로명주소 추출
        List<String> refineRoadnmAddrs = gyeongGiList.gyeongGiApiResponses().stream()
                .map(GyeongGiList.GyeongGiApiResponse::refineRoadnmAddr)
                .collect(Collectors.toList());

        // 추출한 도로명주소로 기존 데이터를 데이터베이스에서 조회
        List<Genrestrt> existingList = genrestrtRepository.findByRefineRoadnmAddrIn(refineRoadnmAddrs);

        // 조회한 데이터를 Map으로 변환 (키: 사업장명|도로명주소, 값: Genrestrt 객체)
        return existingList.stream()
                .collect(Collectors.toMap(
                        genrestrt -> genrestrt.getBizplcNm() + "|" + genrestrt.getRefineRoadnmAddr(),
                        Function.identity(),
                        (existing, replacement) -> existing // 중복 키 처리
                ));
    }

    /*
     * 기존 맛집 원본데이터의 주요 필드의 변경사항 여부를 확입합니다.
     * @param 기존에 존재하던 existing, api에서 받아온 새로운 newData
     * @return 주요 필드 중 하나라도 변경되면 true 반환
     * (시군명, 시군코드, 사업장명, 영업상태명, 위생업종명, 위생업태명, 소재지 도로명주소, 위도, 경도)
     * */
    private boolean hasChanged(Genrestrt existing, GyeongGiList.GyeongGiApiResponse newData){
        // 주요 필드들을 비교하며 변경 여부 확인
        return !existing.getSigunNm().equals(newData.sigunNm())||
                !existing.getSigunCd().equals(newData.sigunCd())||
                !existing.getBizplcNm().equals(newData.bizplcNm())||
                !existing.getBsnStateNm().equals(newData.bsnStateNm())||
                !existing.getSanittnIndutypeNm().equals(newData.sanittnIndutypeNm())||
                !existing.getSanittnBizcondNm().equals(newData.sanittnBizcondNm())||
                !existing.getRefineRoadnmAddr().equals(newData.refineRoadnmAddr())||
                !existing.getRefineWgs84Lat().equals(newData.refineWgs84Lat())||
                !existing.getRefineWgs84Logt().equals(newData.refineWgs84Logt());
    }

    /*
     * 기존 맛집 원본데이터의 주요 필드에 변경사항을 업데이트합니다.
     * @param 기존에 존재하던 existingGenrestrt, 새로운 newData
     * @return 기존에 존재하던 existingGenrestrt
     * */
    private void updateExistingGenrestrt(Genrestrt existingGenrestrt,
                                         GyeongGiList.GyeongGiApiResponse newData) {
        Genrestrt updatedGenrestrt = convertToGenrestrt(newData);
        existingGenrestrt.updateExistingGenrestrt(updatedGenrestrt);
        genrestrtRepository.save(existingGenrestrt); //명시적 저장
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
}
