package wanted.ribbon.datapipe.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import wanted.ribbon.datapipe.dto.RawData;
import wanted.ribbon.datapipe.mapper.RawDataRowMapper;
import wanted.ribbon.datapipe.service.DataProcessor;
import wanted.ribbon.exception.BaseException;
import wanted.ribbon.exception.ErrorCode;
import wanted.ribbon.store.domain.Store;

import java.util.List;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class DataPipeTasklet implements Tasklet {
    private final JdbcTemplate jdbcTemplate; // 데이터베이스 접근
    private final DataProcessor dataProcessor; // RawData를 Store로 변환

    private static final int PAGE_SIZE = 1000; // 페이지 당 데이터 개수

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        int offset = 0; // 데이터 조회 시작 위치
        int totalRowsProcessed = 0; // 총 처리된 데이터 수를 추적하기 위한 변수

        List<RawData> rawDataList; // 현재 페이지에서 조회된 RawData 리스트
        do {
            // genrestrts 테이블에서 영업 중인 데이터와 처리되지 않은 데이터를 페이지 단위로 조회
            String sql = "SELECT sigun_nm, bizplc_nm, bsn_state_nm, sanittn_bizcond_nm, refine_roadnm_addr, refine_wgs84lat, refine_wgs84logt " +
                    "FROM genrestrts " +
                    "WHERE bsn_state_nm = '영업' AND processed = false " +
                    "LIMIT ? OFFSET ?";

            // JdbcTemplate를 데이터 조회
            rawDataList = jdbcTemplate.query(sql,
                    new Object[]{PAGE_SIZE, offset},
                    (rs, rowNum) -> new RawDataRowMapper().mapRow(rs, rowNum)
            );
            // 조회된 데이터가 없으면 반복 종료
            if (rawDataList.isEmpty()) {
                break;
            }

            int count = 0; // 현재 페이지에서 처리된 데이터의 수
            for (RawData rawData : rawDataList) {
                Store store;
                try {
                    // RawData를 Store 객체로 변환
                    store = dataProcessor.process(rawData);
                } catch (Exception e) {
                    // 데이터 처리 중 예외 발생 시 서버 에러
                    throw new BaseException(ErrorCode.DATA_PIPE_TASKLET_ERROR, e.getMessage());
                }

                // Store 객체의 정보를 데이터베이스에 삽입
                jdbcTemplate.update("INSERT INTO stores (sigun, store_name, category, address, store_lat, store_lon, location, rating, review_count) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ST_GeomFromText(CONCAT('POINT(', ?, ' ', ?, ')'), 4326), ?, ?)",
                        store.getSigun(),
                        store.getStoreName(),
                        store.getCategory().name(), // enum 값 문자열로 변환
                        store.getAddress(),
                        store.getStoreLat(),
                        store.getStoreLon(),
                        store.getStoreLon(),  // POINT(lon lat) -> 경도(lon), 위도(lat)
                        store.getStoreLat(),
                        0.0,
                        0); // 평점은 0.0, 리뷰수는 0 디폴트값 입력

                // genrestrts 테이블에서 조회된 데이터는 processed 상태 true로 업데이트
                jdbcTemplate.update("UPDATE genrestrts SET processed = true WHERE bizplc_nm = ? AND refine_roadnm_addr = ?",
                        rawData.getBizplcNm(), rawData.getRefineRoadnmAddr());

                log.info("데이터 삽입 성공 및 처리 상태 업데이트: {}", store); // 맛집 상세 정보를 받을 것인가?
                count++; // 처리된 데이터 수 증가
            }

            totalRowsProcessed += count; // 총 처리된 데이터 수 누적
            offset += PAGE_SIZE; // 다음 페이지로 이동하기 위해 오프셋 증가

            // 현재 페이지 처리 완료 로그, 처리된 데이터 개수 로그
            log.info("페이지 처리 완료: {}개 행 처리됨", count);

        } while (rawDataList.size() == PAGE_SIZE); // 모든 데이터가 처리되지 않은 경우 반복

        // 최종 보고 로그
        log.info("배치 작업 완료. 총 {}개의 행이 처리되었습니다.", totalRowsProcessed);

        return RepeatStatus.FINISHED; // 배치 작업 완료 상태 반환
    }
}
