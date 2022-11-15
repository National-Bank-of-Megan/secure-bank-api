package pl.edu.pw.service.devices;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.UserAgentParser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Device;
import pl.edu.pw.dto.DeviceDTO;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.repository.DeviceRepository;
import pl.edu.pw.util.http.HttpRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DevicesServiceImpl implements DevicesService {
    private static final Logger log = LoggerFactory.getLogger(DevicesServiceImpl.class);
    private final UserAgentParser userAgentParser;
    private final DeviceRepository deviceRepository;
    private final AccountRepository accountRepository;

    private final String DEVICE_FINGERPRINT_HEADER = "Device-Fingerprint";

    @Override
    public boolean verifyDevice(HttpServletRequest request) {
        log.info("Verifying device...");
        String ip = HttpRequestUtils.getClientIpAddressFromRequest(request);
//        TODO multiple devices can share the same ip address, make list
        Optional<Device> device = deviceRepository.findByIp(ip);
        return device.isPresent();
    }

    @Override
    public boolean verifyDeviceByFingerprintAndClientId(String fingerprint,String clientId) {
        log.info("Verifying device by fingerprint");
        Optional<Device> device = deviceRepository.findByFingerprintAndAccountClientId(fingerprint,clientId);
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

    @Override
    public void updateDeviceLogInDate(Device loggedDevice) { // TODO: update ip address on every login?
        loggedDevice.setLastLoggedIn(LocalDateTime.now());
        deviceRepository.save(loggedDevice);
    }

    @Override
    public List<DeviceDTO> getAccountVerifiedDevices(String clientId, String deviceFingerprint) {
        List<DeviceDTO> deviceDTOList = deviceRepository.findAllByAccountClientId(clientId).stream().map(DeviceMapper::map).toList();
        Device currentDevice = deviceRepository.findByFingerprintAndAccountClientId(deviceFingerprint, clientId).orElse(null);
        if (currentDevice != null) {
            DeviceDTO currentDeviceDTO = deviceDTOList.stream().filter(
                    deviceDTO -> deviceDTO.getId().equals(currentDevice.getId())).findFirst().get();
            currentDeviceDTO.setCurrentDevice(true);
        }
        return deviceDTOList;
    }

    @Override
    public void deleteDeviceFromTrustedDevices(Long deviceId, String clientId) {
        deviceRepository.deleteById(deviceId);
    }

    public static class DeviceMapper {
        public static DeviceDTO map(Device device) {
            return new DeviceDTO(
                    device.getId(),
                    device.getName(),
                    device.getIp(),
                    device.getRegistrationDate(),
                    device.getLastLoggedIn(),
                    false
            );
        }
    }

    @Override
    public void registerDevice(HttpServletRequest request, String clientId) {
        String fingerprint = request.getHeader(DEVICE_FINGERPRINT_HEADER);
//        czy to sie zapetli?
        String deviceName = HttpRequestUtils.getDeviceNameFromRequest(request, this);
        String requestPublicIp = HttpRequestUtils.getClientIpAddressFromRequest(request);
        Account account = accountRepository.findById(clientId).orElseThrow(
                () -> new ResourceNotFoundException("Account with given clientId does not exist")
        );
        log.info("Device fingerprint: " + fingerprint);
        Device trustedDevice = new Device(request.getHeader("Device-Fingerprint"), deviceName, LocalDateTime.now(), requestPublicIp);
        trustedDevice.setLastLoggedIn(LocalDateTime.now());
        this.saveDevice(clientId, trustedDevice);
    }
}
