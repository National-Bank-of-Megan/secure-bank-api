package pl.edu.pw.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.domain.Currency;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class ExternalCurrencyApiUtil {

    private static final Logger log = LoggerFactory.getLogger(ExternalCurrencyApiUtil.class);

    public static double exchangeCurrency(Currency currencySold, double amountSold, LocalDateTime time, Currency currencyBought) throws IOException {

//        connect
        String url_str = "https://api.exchangerate.host/convert?from=" + currencySold + "&to=" + currencyBought + "&date=" + time;
        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

//        parse response
        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        String req_result = jsonobj.get("result").getAsString();
        double bought = Double.valueOf(req_result) * amountSold;
        log.info("Selling " + amountSold + currencySold + " for " + bought + currencyBought);

        return bought;
    }
}
