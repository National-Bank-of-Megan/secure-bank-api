package service;

import dto.CurrencyExchangeRequest;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

public interface CurrencyExchangeService {

        void exchangeCurrency(CurrencyExchangeRequest request);


}
