package pl.edu.pw.exchange.service;

import pl.edu.pw.core.dto.CurrencyExchangeDto;
import pl.edu.pw.exchange.dto.CurrencyExchangeRequest;

import java.util.List;

public interface CurrencyExchangeService {

    void exchangeCurrency(CurrencyExchangeRequest request);

    List<CurrencyExchangeDto> getUserExchanges();


}
