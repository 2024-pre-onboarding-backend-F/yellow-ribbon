package wanted.ribbon.datapipe.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import wanted.ribbon.datapipe.component.JobCompletionNotificationListener;
import wanted.ribbon.datapipe.dto.GyeongGiList;
import wanted.ribbon.datapipe.service.RawDataService;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class OpenApiJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RawDataService rawDataService;

    // 1. Job 설정 (OpenApi 데이터 수집, db 데이터 검증, chunk방식 사용)
    @Bean
    public Job openApiJob(JobCompletionNotificationListener listener){
        return new JobBuilder("openApiJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener) // job 관련 예외 처리
                .start(openApiStep())
                .build();
    }

    // 2. Step 설정(공공데이터 수집)
    @Bean
    public Step openApiStep(){
        return new StepBuilder("openApiStep",jobRepository)
                .<String, GyeongGiList> chunk(1,transactionManager) // 입력타입, 출력타입, 청크 크기 정의
                .reader(serviceNameReader())
                .processor(rawDataProcessor())
                .writer(dataWriter())
                .faultTolerant() // 오류 허용 설정 시작
                .skip(Exception.class) // 모든 예외에 스킵 허용
                .skipLimit(5) // 최대 5번까지 스킵 허용
                .build();
    }

    // 2-1. Reader 설정
    @Bean
    @StepScope
    public ListItemReader<String> serviceNameReader() {
        List<String> serviceNames = List.of(
                "Genrestrtchifood", "Genrestrtlunch", "Genrestrtcate", "Genrestrtmovmntcook",
                "Genrestrtjpnfood", "Genrestrtsoup", "Genrestrtfastfood", "Genrestrtsash",
                "Genrestrtbuff", "Genrestrtfugu", "Genrestrtstandpub", "Genrestrtbsrpcook",
                "Genrestrttratearm"
        );
        return new ListItemReader<>(serviceNames);
    }

    // 2-2. Processor 설정
    @Bean
    @StepScope
    public ItemProcessor<String,GyeongGiList> rawDataProcessor() {
        return serviceName -> rawDataService.getAndSaveByServiceName(serviceName).block(); // 동기적으로 처리
    }

    // 2-3 Writer 설정
    @Bean
    @StepScope
    public ItemWriter<GyeongGiList> dataWriter() {
        return items -> {
            for (GyeongGiList item : items) {
                if (item != null) {
                    String message = String.format("%s의 맛집 원본데이터 중 %d개가 추가되고, %d개가 업데이트 됐으며, %d개는 변경사항 없습니다.",
                            item.gyeongGiApiResponses().get(0).sanittnBizcondNm(),
                            item.newCount(),
                            item.updatedCount(),
                            item.unchangedCount());
                    log.info(message);
                    log.info("{}의 맛집 데이터 수집 및 업데이트 총 {}개의 항목 완료", item.gyeongGiApiResponses().get(0).sanittnBizcondNm(), item.total());
                } else {
                    log.warn("수집된 데이터가 없거나 비었습니다.");
                }
            }
        };
    }
}
