package pl.edu.pw.security.access;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.pw.domain.Device;
import pl.edu.pw.repository.DeviceRepository;

@Component
@RequiredArgsConstructor
public class AccountSecurity {

    private final DeviceRepository deviceRepository;

    public boolean isDeviceAttachedToAccount(Long deviceId, String clientId) {
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device == null) {
            return false;
        }
        return device.getAccount().getClientId().equals(clientId);
    }
}
