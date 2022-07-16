package service;

import dto.CurrencyExchangeRequest;
import org.json.JSONObject;

public interface CurrencyExchangeService {

        void exchangeCurrency(CurrencyExchangeRequest request);


}
