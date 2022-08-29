package pl.edu.pw.service.devices;

import pl.edu.pw.domain.Device;
import pl.edu.pw.dto.DeviceDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface DevicesService {

    boolean verifyDevice(HttpServletRequest request);

    void saveDevice(String clientId, Device device);

    String getDeviceName(String header);

    void updateDeviceLogInDate(Device loggedDevice);

    List<DeviceDTO> getAccountVerifiedDevices(String clientId, String deviceFingerprint);

    void deleteDeviceFromTrustedDevices(Long deviceId, String clientId);
}
