package pl.edu.pw.auth.service.account;

import pl.edu.pw.auth.dto.VerifyDeviceWithCodeRequest;

import javax.servlet.http.HttpServletRequest;

public interface VerificationService {
    boolean verifyDevice(VerifyDeviceWithCodeRequest request, HttpServletRequest httpRequest);
}
