package pl.edu.pw.util.http;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import pl.edu.pw.service.devices.DevicesService;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class HttpRequestUtils {
    private final String[] IP_HEADER_CANDIDATES = {
            "X-Real-IP",
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public String getClientIpAddressFromRequest(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                String ip = ipList.split(",")[0];
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public String getDeviceNameFromRequest(HttpServletRequest request, DevicesService devicesService) {
        String userAgentHeaderValue = request.getHeader(HttpHeaders.USER_AGENT);
        if (userAgentHeaderValue == null) {
            throw new RuntimeException("User-Agent header is required");
        }
        String deviceName = devicesService.getDeviceName(userAgentHeaderValue);
        if (deviceName == null || deviceName.isBlank()) {
            throw new RuntimeException("Server could not get name of your device");
        }
        return deviceName;
    }
}
