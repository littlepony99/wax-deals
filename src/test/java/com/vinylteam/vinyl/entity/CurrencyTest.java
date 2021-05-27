package com.vinylteam.vinyl.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyTest {

    @Test
    @DisplayName("Tests if we get optional with right currency by currency symbols like €.")
    void getCurrencyBySymbolTest() {
        assertEquals(Currency.EUR, Currency.getCurrency("&nbsp;€").get());
        assertEquals(Currency.EUR, Currency.getCurrency("€").get());
        assertEquals(Currency.GBP, Currency.getCurrency("£").get());
        assertEquals(Currency.USD, Currency.getCurrency("$").get());
        assertEquals(Currency.UAH, Currency.getCurrency("₴").get());
    }

    @Test
    @DisplayName("Tests if we get optional with right currency by currency names like EUR.")
    void getCurrencyByNameTest() {
        assertEquals(Currency.EUR, Currency.getCurrency(" EUR").get());
        assertEquals(Currency.EUR, Currency.getCurrency("EUR").get());
        assertEquals(Currency.GBP, Currency.getCurrency("GBP").get());
        assertEquals(Currency.USD, Currency.getCurrency("USD").get());
        assertEquals(Currency.UAH, Currency.getCurrency("грн").get());
        assertEquals(Currency.UAH, Currency.getCurrency("грн.").get());
    }

    @Test
    @DisplayName("Tests if we get empty optional by wrong strings.")
    void getCurrencyEmptyTest() {
        assertTrue(Currency.getCurrency(" EUR GBPUSDUAH").isEmpty());
        assertTrue(Currency.getCurrency("£$").isEmpty());
        assertTrue(Currency.getCurrency("₴₴").isEmpty());
        assertTrue(Currency.getCurrency("12").isEmpty());
        assertTrue(Currency.getCurrency("hryvna").isEmpty());
    }

    @Test
    @DisplayName("Tests if we get empty optional by null currencyDescriptor")
    void getCurrencyNullTest() {
        assertTrue(Currency.getCurrency(null).isEmpty());
    }

}