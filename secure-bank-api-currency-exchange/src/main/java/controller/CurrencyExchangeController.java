package controller;

import dto.CurrencyExchangeRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CurrencyExchangeService;

@RestController
@RequestMapping(path="/api/exchange")
@AllArgsConstructor
public class CurrencyExchangeController {
//    @RequestBody CurrencyExchangeRequest request
    private  CurrencyExchangeService currencyExchangeService;

    @PostMapping("/")
    public ResponseEntity exchangeCurrency(){
        currencyExchangeService.exchangeCurrency(new CurrencyExchangeRequest());
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/")
    public String elo(){
        return "e;p";
    }
}
