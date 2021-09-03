package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.service.impl.DefaultOneVinylOffersService;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oneVinyl")
public class OneVinylOfferController {

    private final DefaultOneVinylOffersService oneVinylOffersService;

    @GetMapping("/{id}")
    public OneVinylPageDto getOneVinylOfferPage(@PathVariable("id") String id) {
        return oneVinylOffersService.prepareOneVinylInfo(id);
    }

}
