package com.vinylteam.vinyl.entity;

import java.util.Optional;

import static java.util.Optional.ofNullable;

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
        Currency resultingCurrency = null;
        if ("грн".equals(currencyDescription) || "грн.".equals(currencyDescription) || "₴".equals(currencyDescription)) {
            resultingCurrency = UAH;
        } else if ("GBP".equals(currencyDescription) || "£".equals(currencyDescription)) {
            resultingCurrency = GBP;
        } else if ("USD".equals(currencyDescription) || "$".equals(currencyDescription)) {
            resultingCurrency = USD;
        } else if ("EUR".equals(currencyDescription.trim()) || "€".equals(currencyDescription)
                || "&nbsp;€".equals(currencyDescription)) {
            resultingCurrency = EUR;
        }
        return ofNullable(resultingCurrency);
    }

}
