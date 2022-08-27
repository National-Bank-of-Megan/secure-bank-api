package pl.edu.pw.service.devices;

import com.blueconic.browscap.BrowsCapField;
import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Device;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.repository.DeviceRepository;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DevicesServiceImpl implements DevicesService {
    private static final Logger log = LoggerFactory.getLogger(DevicesServiceImpl.class);

    private final UserAgentParser userAgentParser;
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
    private final AccountRepository accountRepository;

    @Override
    public boolean verifyDevice(HttpServletRequest request) {
        log.info("Veryfing device...");
        String ip = getIpAddress(request);
        Optional<Device> device = deviceRepository.findByIp(ip);
        return device.isPresent();
    }

    @Override
    public void saveDevice(String clientId, Device device) {
        Account account = accountRepository.findById(clientId).orElseThrow(
                () -> new RuntimeException("Something went wrong with fetching your account")
        );
        account.addDevice(device);
    }

    @Override
    public String getDeviceName(String header) {
        if (header == null) {
            throw new IllegalArgumentException("User-Agent header was not provided");
        }
        Capabilities capabilities = userAgentParser.parse(header);
        if (capabilities == null) {
            return null;
        }
        return capabilities.getBrowser() + " " + capabilities.getBrowserType() + " " + capabilities.getBrowserMajorVersion() + " - "
               + capabilities.getDeviceType() + " - " + capabilities.getPlatform() + " " + capabilities.getPlatformVersion();
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
