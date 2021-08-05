package com.vinylteam.vinyl.service.converter;

import com.vinylteam.vinyl.entity.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyToStringConverterTest {
    private CurrencyToStringConverter currencyToStringConverter = new CurrencyToStringConverter();

    @Test
    @DisplayName("convert currency to string")
    void convert() {
        String actual = currencyToStringConverter.convert(Optional.of(Currency.UAH));
        assertEquals("₴", actual);
    }

    @Test
    @DisplayName("convert empty value")
    void convertEmpty() {
        String actual = currencyToStringConverter.convert(Optional.empty());
        assertEquals("", actual);
    }

}