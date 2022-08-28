package pl.edu.pw.service.account;

import pl.edu.pw.dto.VerifyDeviceWithCodeRequest;

import javax.servlet.http.HttpServletRequest;

public interface VerificationService {
    boolean verifyDevice(VerifyDeviceWithCodeRequest request, HttpServletRequest httpRequest);
}
