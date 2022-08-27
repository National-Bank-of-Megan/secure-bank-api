package pl.edu.pw.service.devices;

import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Device;

import javax.servlet.http.HttpServletRequest;

public interface DevicesService {

    boolean verifyDevice(HttpServletRequest request);

    void saveDevice(String clientId, Device device);

    String getDeviceName(String header);
}
