package pl.edu.pw.infrastructure.kafka.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.edu.pw.logic.model.PendingTransfer;

@Component
@RequiredArgsConstructor
public class KafkaMessagesListener {

    @KafkaListener(topics = "${kafka.topic.pendingTransfersBus}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload PendingTransfer pendingTransfer) {

    }
}
