package pl.edu.pw.service;

import pl.edu.pw.dto.CurrencyExchangeDto;
import pl.edu.pw.dto.CurrencyExchangeRequest;

import java.util.List;

public interface CurrencyExchangeService {

    void exchangeCurrency(CurrencyExchangeRequest request);

    List<CurrencyExchangeDto> getUserExchanges();


}
