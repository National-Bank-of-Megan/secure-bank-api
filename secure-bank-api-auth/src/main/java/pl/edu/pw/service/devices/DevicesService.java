package pl.edu.pw.service.devices;

import pl.edu.pw.domain.Device;
import pl.edu.pw.dto.DeviceDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DevicesService {

    boolean verifyDevice(HttpServletRequest request);

    boolean verifyDeviceByFingerprintAndClientId(String fingerprint,String clientId);

    void saveDevice(String clientId, Device device);

    String getDeviceName(String header);

    void updateDeviceLogInDate(Device loggedDevice);

    List<DeviceDTO> getAccountVerifiedDevices(String clientId, String deviceFingerprint);

    void deleteDeviceFromTrustedDevices(Long deviceId, String clientId);

    void registerDevice(HttpServletRequest request, String clientId);

    void setExpoPushToken(String clientId, String expoPushToken);
}
