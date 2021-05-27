package com.vinylteam.vinyl.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public enum Currency {

    UAH("₴"),
    USD("$"),
    GBP("£"),
    EUR("€");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Optional<Currency> getCurrency(String currencyDescription) {
        Logger logger = LoggerFactory.getLogger(Currency.class);
        logger.debug("getCurrency started with {currencyDescription':{}}", currencyDescription);
        Currency resultingCurrency = null;
        if ("грн".equals(currencyDescription) || "грн.".equals(currencyDescription) || "₴".equals(currencyDescription)) {
            resultingCurrency = UAH;
        } else if ("GBP".equals(currencyDescription) || "£".equals(currencyDescription)) {
            resultingCurrency = GBP;
        } else if ("USD".equals(currencyDescription) || "$".equals(currencyDescription)) {
            resultingCurrency = USD;
        } else if (" EUR".equals(currencyDescription) || "EUR".equals(currencyDescription) || "€".equals(currencyDescription)
                || "&nbsp;€".equals(currencyDescription)) {
            resultingCurrency = EUR;
        }
        logger.debug("Resulting optional with currency is {'resultingOptionalCurrency':{}}",
                Optional.ofNullable(resultingCurrency));
        return Optional.ofNullable(resultingCurrency);
    }

}
