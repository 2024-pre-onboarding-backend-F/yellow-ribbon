package wanted.ribbon.datapipe.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job이 성공적으로 끝났습니다. 결과를 확인하세요.");
        }
        else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Job 작업을 실패했습니다. 실패 이유를 확인하세요.");
            List<Throwable> exceptions = jobExecution.getAllFailureExceptions();
            for (Throwable throwable : exceptions) {
                log.error("Job을 처리하는 동안 발생한 예외입니다. {}", exceptions);
            }
        }
    }
}
