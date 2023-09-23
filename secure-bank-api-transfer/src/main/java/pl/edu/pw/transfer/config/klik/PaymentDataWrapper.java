package pl.edu.pw.transfer.config.klik;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import pl.edu.pw.transfer.dto.PaymentRequest;

@Builder
@Getter
public class PaymentDataWrapper {

    private WebSocketSession webSocketSession;
    private PaymentRequest paymentRequest;
}
