package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.service.account.AccountService;

@RestController
@RequestMapping(path = "/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;


}
