package pl.edu.pw.transfer.infrastructure.kafka.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.edu.pw.transfer.logic.integration.MessagesProcessing;
import pl.edu.pw.transfer.logic.model.PendingTransfer;

@Component
@RequiredArgsConstructor
public class KafkaTransfersListener {

    private final MessagesProcessing messagesProcessing;

    @KafkaListener(topics = "${kafka.topic.pendingTransfersBus}", containerFactory = "kafkaListenerContainerFactory", autoStartup = "false")
    public void listen(@Payload PendingTransfer pendingTransfer) {
        messagesProcessing.processPendingTransfer(pendingTransfer);
    }
}
