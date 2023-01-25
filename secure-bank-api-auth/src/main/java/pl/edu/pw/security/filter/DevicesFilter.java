package pl.edu.pw.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import pl.edu.pw.service.devices.DevicesService;
import pl.edu.pw.util.http.CustomHttpServletRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class DevicesFilter extends ClientIdContainer {

    private static final Logger log = LoggerFactory.getLogger(DevicesFilter.class);
    private final DevicesService devicesService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String clientId = ClientIdContainer.clientId;
        log.info("Request {}", request.getServletPath());
//        TODO create enum containing paths
        if (request.getServletPath().equals("/api/account/device/register") || request.getServletPath().contains("/api/web/login") || request.getServletPath().equals("/api/web/register")
                || request.getServletPath().equals("/api/transfer/notification/subscribe") || request.getServletPath().equals("/api/web/token/refresh")
                || request.getServletPath().equals("/payment/finalize")
        ) {
            if (request.getServletPath().equals("/api/account/device/register"))
                devicesService.registerDevice(request, clientId);
            log.info("filtering");
            filterChain.doFilter(new CustomHttpServletRequestWrapper(request), response);
        } else {
            if (devicesService.verifyDeviceByFingerprintAndClientId(request.getHeader("Device-Fingerprint"), clientId))
                filterChain.doFilter(new CustomHttpServletRequestWrapper(request), response);
            else {
                Map<String, String> error = new HashMap<>();
                error.put("error_message", "This device is not authorized to access this resource");
                response.setStatus(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
    }
}
