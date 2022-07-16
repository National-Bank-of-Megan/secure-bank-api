package controller;

import dto.CurrencyExchangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.CurrencyExchangeService;

@RestController("/api/exchange")
@RequiredArgsConstructor
public class CurrencyExchangeController {
//    @RequestBody CurrencyExchangeRequest request
    private final CurrencyExchangeService currencyExchangeService

    @PostMapping("/")
    public ResponseEntity exchangeCurrency(){
        currencyExchangeService.exchangeCurrency(new CurrencyExchangeRequest());
        return new ResponseEntity(HttpStatus.OK);
    }
}
