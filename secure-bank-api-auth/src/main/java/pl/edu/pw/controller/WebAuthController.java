package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.service.account.AccountService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/web")
@RequiredArgsConstructor
public class WebAuthController {

    private static final Logger log = LoggerFactory.getLogger(WebAuthController.class);
    private final AccountService accountService;

    @GetMapping("/login/combination")
    public String getLoginCombination(@Valid @RequestParam String username) {
        return accountService.getLoginCombination(username);
    }






}
