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
import wanted.ribbon.exception.BaseException;
import wanted.ribbon.exception.ErrorCode;
import wanted.ribbon.datapipe.dto.RawData;
import wanted.ribbon.datapipe.mapper.RawDataRowMapper;
import wanted.ribbon.datapipe.mapper.StoreDataRowMapper;
import wanted.ribbon.datapipe.service.DataProcessor;
import wanted.ribbon.store.domain.Store;

import java.util.ArrayList;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class DataFetchTasklet implements Tasklet {
    private final JdbcTemplate jdbcTemplate;
    private final DataProcessor dataProcessor;

    private static final int PAGE_SIZE = 1000;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        int offset = 0;
        int totalRowsFetched = 0;
        int totalRowsDeleted = 0;

        List<RawData> rawDataList;

        do {
            // genrestrts 테이블에서 처리된 데이터를 페이지 단위로 조회
            String autoSql = "SELECT sigun_nm, bizplc_nm, bsn_state_nm, sanittn_bizcond_nm,refine_roadnm_addr,refine_wgs84lat,refine_wgs84logt " +
                    "FROM genrestrts " +
                    "WHERE processed = true " +
                    "LIMIT ? OFFSET ?";

            // JdbcTemplate를 사용해 데이터 조회
            rawDataList = jdbcTemplate.query(autoSql,
                    new Object[]{PAGE_SIZE,offset},
                    (rs, rowNum) -> new RawDataRowMapper().mapRow(rs, rowNum)
            );
            // 조회된 데이터가 없으면 반복 종료
            if (rawDataList.isEmpty()) {
                break;
            }

            for (RawData rawData : rawDataList) {
              try{
                  // 1. 영업 상태가 페업으로 변한 경우
                  if ("폐업".equals(rawData.getBsnStateNm())){
                      // stores에서 데이터 삭제
                      String deleteSql = "DELETE FROM stores " +
                              "WHERE store_name = ? AND address = ?";
                      int rowsDeleted = jdbcTemplate.update(deleteSql,rawData.getBizplcNm(),rawData.getRefineRoadnmAddr());

                      // 로그에 삭제된 맛집 정보 알림
                      if (rowsDeleted > 0) {
                          log.info("삭제된 맛집: 이름={}, 주소={}", rawData.getBizplcNm(), rawData.getRefineRoadnmAddr());
                      }

                      // genrestrts에서 processed 필드 false로 업데이트
                      String updateGenrestrtsSql = "UPDATE genrestrts SET processed = false " +
                              "WHERE bizplc_nm = ? AND refine_roadnm_addr = ?";
                      jdbcTemplate.update(updateGenrestrtsSql,rawData.getBizplcNm(),rawData.getRefineRoadnmAddr());

                      // 삭제된 행수 업데이트
                      totalRowsDeleted += rowsDeleted;
                      continue;
                  }
                  // 2. 영업 상태가 폐업이 아닌 경우
                  Store store = dataProcessor.process(rawData);

                  // stores에서 RawData와 매핑된 레코드 조회
                  String storeQuery = "SELECT sigun, store_name, category, address, store_lat, store_lon,rating " +
                          "FROM stores " +
                          "WHERE store_name = ? AND address = ?";
                  List<Store> storeDataList = jdbcTemplate.query(storeQuery,
                          new Object[]{rawData.getBizplcNm(),rawData.getRefineRoadnmAddr()},
                          new StoreDataRowMapper());

                  // db에서 받아온 Store 데이터와 새롭게 받아온 RawData 비교
                  if(!storeDataList.isEmpty()) {
                      Store existingStore = storeDataList.get(0);
                      // 업데이트 필요 필드 추적
                      List<Object> updateParams = new ArrayList<>();
                      StringBuilder updateSql = new StringBuilder("UPDATE stores SET ");

                      // 데이터 비교
                      if (!existingStore.getSigun().equals(store.getSigun())) {
                          updateSql.append("sigun = ?, ");
                          updateParams.add(store.getSigun());
                      }
                      if (!existingStore.getStoreName().equals(store.getStoreName())) {
                          updateSql.append("store_name = ?, ");
                          updateParams.add(store.getStoreName());
                      }
                      if (!existingStore.getCategory().equals(store.getCategory())) {
                          updateSql.append("category = ?, ");
                          updateParams.add(store.getCategory());
                      }
                      /*
                       * 1. 도로명 주소가 변경된 경우
                       * 2. 위도, 경도 함께 변경된 경우가 많지 않을까?
                       * 3. 도로명주소, 위도, 경도 같이 업데이트
                       */
                      if (!existingStore.getAddress().equals(store.getAddress())) {
                          updateSql.append("address = ?, ");
                          updateParams.add(store.getAddress());
                          updateSql.append("store_lat = ?, "); // 맛집 위도
                          updateParams.add(store.getStoreLat());
                          updateSql.append("store_lon = ?, "); // 맛집 경도
                          updateParams.add(store.getStoreLon());
                      }
                      // 변경된 필드가 있는 경우에만 업데이트 실행
                      if (!updateParams.isEmpty()) {
                          updateSql.setLength(updateSql.length()-2); // 마지막 필드 뒤의 쉼표 제거
                          updateSql.append("WHERE store_name = ? AND address = ?");
                          updateParams.add(rawData.getBizplcNm());
                          updateParams.add(rawData.getRefineRoadnmAddr());

                          int rowsFetched = jdbcTemplate.update(updateSql.toString(),updateParams.toArray());

                          if (rowsFetched > 0) {
                              log.info("변경된 맛집: 이름={}, 도로명 주소={}", rawData.getBizplcNm(), rawData.getRefineRoadnmAddr());
                          }
                          totalRowsFetched += rowsFetched; // 총 변경 처리된 데이터 수 누적
                      }
                  }
              } catch (Exception e) {
                  throw new BaseException(ErrorCode.DATA_PIPE_TASKLET_ERROR,e.getMessage());
              }
            }
            offset += PAGE_SIZE; // 다음 페이지로 이동하기 위해 오프셋 증가
        } while (!rawDataList.isEmpty());

        // 최종 보고 로그
        log.info("정보가 바뀐 맛집의 수는: {}", totalRowsFetched);
        log.info("정보가 지워진 맛집의 수는: {}", totalRowsDeleted);

        return RepeatStatus.FINISHED; // 배치 작업 완료 상태 반환
    }
}
