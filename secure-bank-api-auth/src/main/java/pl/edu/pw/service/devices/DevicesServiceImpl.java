package pl.edu.pw.service.devices;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Device;
import pl.edu.pw.repository.DeviceRepository;
import pl.edu.pw.domain.Account;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DevicesServiceImpl implements DevicesService {


    private static final Logger log = LoggerFactory.getLogger(DevicesServiceImpl.class);
    private static final String[] IP_HEADERS = {
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
    private final DeviceRepository deviceRepository;

    @Override
    public boolean verifyDevice( HttpServletRequest request) {
        log.info("Veryfing device...");
        String ip = getIpAddress(request);
        Optional<Device> device= deviceRepository.findByIp(ip);
        if(device.isPresent()) return true;
        return false;
    }

    @Override
    public void saveDevice(Account user, HttpServletRequest request) {

    }

    public String getIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String value = request.getHeader(header);
            if (value == null || value.isEmpty()) {
                continue;
            }
            String[] parts = value.split("\\s*,\\s*");
            return parts[0];
        }
        return request.getRemoteAddr();
    }

}
