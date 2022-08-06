package pl.edu.pw.scheduler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Component
public class FinalizeTransfersJob implements Job {

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        registry.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        registry.stop();
    }
}
