package pl.edu.pw.config.klik;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebSocketPool {
    public static Map<String, Set<WebSocketSession>> websockets = new HashMap<>();
}