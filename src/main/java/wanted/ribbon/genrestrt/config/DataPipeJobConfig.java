package wanted.ribbon.genrestrt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import wanted.ribbon.genrestrt.component.DataPipeTasklet;

@RequiredArgsConstructor
@Configuration
public class DataPipeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataPipeTasklet dataPipeTasklet; // Tasklet을 주입받음 -> 모듈화, 세부 관리

    // 1. Job 설정 (원본데이터 -> 운영테이블 데이터 저장)
    @Bean
    public Job dataPipeJob() {
        return new JobBuilder("dataPipeJob", jobRepository)
                .start(dataPipeStep())
                .build();
    }

    // 2. Step 설정
    @Bean
    public Step dataPipeStep() {
        return new StepBuilder("dataPipeStep", jobRepository)
                .tasklet(dataPipeTasklet, transactionManager) // Tasklet을 Step에 설정
                .build();
    }
}
