package com.vinylteam.vinyl.service.converter;

import com.vinylteam.vinyl.entity.Currency;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StringToCurrencyConverterTest {

    private final StringToCurrencyConverter stringToCurrencyConverter = new StringToCurrencyConverter();

    @Test
    void convert() {
        Optional<Currency> actual = stringToCurrencyConverter.convert("₴");

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(Currency.UAH, actual.get());
    }

    @Test
    void convertNotCorrectCurrency() {
        Optional<Currency> actual = stringToCurrencyConverter.convert("uah");

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

}