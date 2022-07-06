package pl.edu.pw.service.devices;

import pl.edu.pw.domain.Account;

import javax.servlet.http.HttpServletRequest;

public interface DevicesService {

    boolean verifyDevice(HttpServletRequest request );
    void saveDevice(Account user, HttpServletRequest request);
}
