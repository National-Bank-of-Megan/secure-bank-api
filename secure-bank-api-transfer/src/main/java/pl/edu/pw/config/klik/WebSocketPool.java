package pl.edu.pw.config.klik;

import org.springframework.web.socket.WebSocketSession;
import pl.edu.pw.dto.PaymentRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebSocketPool {
    public static Map<String, PaymentDataWrapper> payments = new HashMap<>();
}