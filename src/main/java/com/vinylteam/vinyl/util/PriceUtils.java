package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Currency;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class PriceUtils {

    public static double getPriceFromString(String fullPriceDetails) {
        String priceDetails = fullPriceDetails.replaceAll(",", ".").replaceAll("[^0123456789. ]", "");
        if (priceDetails.length() > 0) {
            try {
                double price = Double.parseDouble(priceDetails);
                log.debug("Got price from price details {'price':{}, 'priceDetails':{}}", price, fullPriceDetails);
                return price;
            } catch (Exception e) {
                log.error("Error while getting price from price details from link {'priceDetails':{}, 'link':{}}", fullPriceDetails);
            }
        }
        log.warn("Can't find price from price details from link, returning 0. {'priceDetails':{}, 'link':{}}", fullPriceDetails);
        return 0.;
    }

    public static Optional<Currency> getCurrencyFromString(String fullPriceDetails) {
        if (fullPriceDetails.length() > 0) {
            String priceDetails = fullPriceDetails;
            try {
                String currency = priceDetails.substring(0, 1);
                Optional<Currency> optionalCurrency = Currency.getCurrency(currency);
                log.debug("Got optional with currency from price details {'optionalCurrency':{}, 'priceDetails':{}}", optionalCurrency, priceDetails);
                return optionalCurrency;
            } catch (Exception e) {
                log.error("Error while getting optional with currency from price details from link {'priceDetails':{}}", priceDetails);
            }
        }
        log.warn("Can't find currency description from price details from link, returning empty optional {'priceDetails':{}}", fullPriceDetails);
        return Optional.empty();
    }

}
