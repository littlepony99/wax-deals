package com.vinylteam.vinyl.service.converter;

import com.vinylteam.vinyl.entity.Currency;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Optional;

@ReadingConverter
public class StringToCurrencyConverter implements Converter<String, Optional<Currency>> {

    @Override
    public Optional<Currency> convert(String source) {
        return Currency.getCurrency(source);
    }

}