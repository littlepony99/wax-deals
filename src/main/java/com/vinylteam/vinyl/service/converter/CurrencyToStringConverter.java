package com.vinylteam.vinyl.service.converter;

import com.vinylteam.vinyl.entity.Currency;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Optional;

@WritingConverter
public class CurrencyToStringConverter implements Converter<Optional<Currency>, String> {

    @Override
    public String convert(Optional<Currency> source) {
        if (source.isEmpty()) {
            return "";
        }
        return source.get().getSymbol();
    }

}