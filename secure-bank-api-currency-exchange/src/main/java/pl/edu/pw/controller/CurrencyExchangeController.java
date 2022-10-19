package pl.edu.pw.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.dto.CurrencyExchangeDto;
import pl.edu.pw.dto.CurrencyExchangeRequest;
import pl.edu.pw.service.CurrencyExchangeService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/exchange")
@AllArgsConstructor
public class CurrencyExchangeController {
    //    @RequestBody CurrencyExchangeRequest request
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
