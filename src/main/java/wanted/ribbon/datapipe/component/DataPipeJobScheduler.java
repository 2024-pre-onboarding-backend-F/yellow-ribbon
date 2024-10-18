package wanted.ribbon.datapipe.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class DataPipeJobScheduler {
    private final JobLauncher jobLauncher;
    private final Job openApiJob;
    private final Job dataPipeJob;

    @Scheduled(cron = "0 0 3 * * 1") // 매주 월요일 새벽 3시
    public void collectRawData(){
        runJob(openApiJob,"공공데이터 수집");
    }

    @Scheduled(cron = "0 0 5 * * 1") // 매주 월요일 새벽 5시
    public void processedDataUpdate(){
        runJob(dataPipeJob,"운영 데이터 전처리");
    }

    private void runJob(Job job, String jobDescription) {
        log.info("{} Job을 실행합니다.",jobDescription);
        try {
            // 각 실행마다 고유한 JobParameters 생성
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            // 작업 실행
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            // 작업 실행 결과
            log.info(" {} Job 결과입니다: {}", jobDescription,jobExecution.getStatus());
        } catch (Exception e) {
            // 예외 발생 시
            log.error("{} Job에 예외가 발생했습니다: {}",jobDescription, e.getMessage());
        }
    }
}
