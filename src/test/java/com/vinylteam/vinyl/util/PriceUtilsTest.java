
/*
package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Currency;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceUtilsTest {

    static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(Currency.EUR, 12.49d, "€ 12,49"),
                Arguments.of(Currency.EUR, 12.49d, "12,49 €"),
                Arguments.of(Currency.USD, 12.49d, "$ 12.49"),
                Arguments.of(Currency.USD, 120000000d, "$120_000_000"),
                Arguments.of(Currency.USD, 120000000d, "$ 120_000_000"),
                Arguments.of(Currency.GBP, 120.12d, "£120.12")
        );
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void getPriceFromString(Currency currency, double amount, String writtenAmount) {
        assertEquals(amount, PriceUtils.getPriceFromString(writtenAmount));
        assertEquals(currency, PriceUtils.getCurrencyFromString(writtenAmount).get());
    }

}*/
