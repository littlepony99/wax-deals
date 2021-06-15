package com.vinylteam.vinyl.util.impl;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParserHolder {

    private final List<VinylParser> parsers;

    public ParserHolder(List<VinylParser> parsers) {
        this.parsers = new ArrayList<>(parsers);
    }

    public Optional<VinylParser> getShopParserByShopId(long shopId){
        return parsers.stream()
                .filter(parser -> parser.getShopId() == shopId)
                .findFirst();
    }

}
