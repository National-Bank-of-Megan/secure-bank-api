package pl.edu.pw.security.access;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.edu.pw.domain.Device;
import pl.edu.pw.repository.DeviceRepository;
import pl.edu.pw.security.config.BankGrantedAuthorities;
import pl.edu.pw.util.CurrentUserUtil;

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

    public boolean doesUserHaveTransferAuthority(){
        return CurrentUserUtil.getCurrentAuthenticationPrincipleAuthorities().contains(new SimpleGrantedAuthority(BankGrantedAuthorities.TRANSFER.toString()));
    }

    public boolean doesUserHaveAccountAuthority(){
        return CurrentUserUtil.getCurrentAuthenticationPrincipleAuthorities().contains(new SimpleGrantedAuthority(BankGrantedAuthorities.ACCOUNT.toString()));
    }

    public boolean doesUserHaveExchangeAuthority(){
        return CurrentUserUtil.getCurrentAuthenticationPrincipleAuthorities().contains(new SimpleGrantedAuthority(BankGrantedAuthorities.EXCHANGE.toString()));
    }

    public boolean doesUserHaveKlikAuthority(){
        return CurrentUserUtil.getCurrentAuthenticationPrincipleAuthorities().contains(new SimpleGrantedAuthority(BankGrantedAuthorities.KLIK.toString()));
    }
}
