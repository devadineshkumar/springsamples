package com.test.springbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

/**
 * Step-level listener that logs step lifecycle events and can adjust the step exit status.
 */
@Component
public class MyStepListener extends StepExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(MyStepListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Step starting: name={}, id={}", stepExecution.getStepName(), stepExecution.getId());
        // step-scoped setup if needed
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Step finished: name={}, id={}, read={}, write={}, skip={}",
                stepExecution.getStepName(), stepExecution.getId(),
                stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getSkipCount());

        // Example: change exit status if too many skips
        if (stepExecution.getSkipCount() > 10) {
            log.warn("Step {} had too many skips: {}", stepExecution.getStepName(), stepExecution.getSkipCount());
            return new ExitStatus("TOO_MANY_SKIPS");
        }
        return stepExecution.getExitStatus();
    }
}
