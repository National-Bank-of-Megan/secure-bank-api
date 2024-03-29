package pl.edu.pw.exchange.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.core.dto.CurrencyExchangeDto;
import pl.edu.pw.exchange.dto.CurrencyExchangeRequest;
import pl.edu.pw.exchange.service.CurrencyExchangeService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/exchange")
@AllArgsConstructor
@PreAuthorize("@accountSecurity.doesUserHaveExchangeAuthority()")
public class CurrencyExchangeController {

    private CurrencyExchangeService currencyExchangeService;

    @PostMapping("/")
    public ResponseEntity<Void> exchangeCurrency(@RequestBody @Valid CurrencyExchangeRequest request) {
        currencyExchangeService.exchangeCurrency(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<CurrencyExchangeDto> getUserExchangesHistory() {
        return currencyExchangeService.getUserExchanges();
    }
}
