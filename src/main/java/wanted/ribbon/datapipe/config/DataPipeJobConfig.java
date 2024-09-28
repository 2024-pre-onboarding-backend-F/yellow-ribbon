package wanted.ribbon.datapipe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import wanted.ribbon.datapipe.component.DataFetchTasklet;
import wanted.ribbon.datapipe.component.DataPipeTasklet;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class DataPipeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager; // jdbc 사용을 위해
    private final DataPipeTasklet dataPipeTasklet; // Tasklet을 주입받음 -> 모듈화, 세부 관리
    private final DataFetchTasklet dataFetchTasklet;

    // 1. Job 설정 (원본데이터 -> 운영테이블 데이터 저장)
    @Bean
    public Job dataPipeJob() {
        return new JobBuilder("dataPipeJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 실행할 때마다 JobParameters에 ID 증가
                .start(dataPipeStep())
                .next(dataFetchStep())
                .build();
    }

    // 2. Step 설정
    @Bean
    public Step dataPipeStep() {
        return new StepBuilder("dataPipeStep", jobRepository)
                .tasklet(dataPipeTasklet, transactionManager) // Tasklet을 Step에 설정
                .allowStartIfComplete(true) // 완료된 후에도 재시작 가능
                .build();
    }

    // 3. Step2 설정
    @Bean
    public Step dataFetchStep() {
        return new StepBuilder("dataFetchStep",jobRepository)
                .tasklet(dataFetchTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
