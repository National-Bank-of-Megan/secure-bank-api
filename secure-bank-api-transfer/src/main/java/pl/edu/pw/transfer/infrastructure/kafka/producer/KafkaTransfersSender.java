package pl.edu.pw.transfer.infrastructure.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.edu.pw.transfer.logic.model.PendingTransfer;
import pl.edu.pw.transfer.logic.producer.TransfersSender;

@Component
@RequiredArgsConstructor
public class KafkaTransfersSender implements TransfersSender {

    private final KafkaTemplate<String, PendingTransfer> kafkaTemplate;

    @Value("${kafka.topic.pendingTransfersBus}")
    private String topic;

    @Override
    @Async
    public void sendPendingTransfer(Long transferId, PendingTransfer pendingTransfer) {
        ProducerRecord<String, PendingTransfer> transferRecord = new ProducerRecord<>(topic, transferId.toString(), pendingTransfer);
        kafkaTemplate.send(transferRecord);
    }
}
