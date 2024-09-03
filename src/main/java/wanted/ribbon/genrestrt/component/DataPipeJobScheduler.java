package wanted.ribbon.genrestrt.component;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wanted.ribbon.genrestrt.config.DataPipeJobConfig;

@Component
@RequiredArgsConstructor
public class DataPipeJobScheduler {
    private final JobLauncher jobLauncher;
    private final DataPipeJobConfig dataPipeJobConfig;

    @Scheduled(cron = "0 0 3 * * 1") // 매주 월요일 새벽 3시 1번 cron 표현식 활용 (초 분 시 일 월 요일)
    public void DataUpdate() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, JobRestartException {
        jobLauncher.run(
                dataPipeJobConfig.dataPipeJob(),
                new JobParametersBuilder()
                        .toJobParameters()
        );
    }
}
