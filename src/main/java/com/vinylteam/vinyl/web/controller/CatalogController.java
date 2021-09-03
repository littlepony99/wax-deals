package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    private final UniqueVinylService uniqueVinylService;
    private final UniqueVinylMapper uniqueVinylMapper;

    @GetMapping
    public List<UniqueVinylDto> getCatalogPage() {
        List<UniqueVinyl> uniqueVinyls = uniqueVinylService.findRandom(50);
        return uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(uniqueVinyls);
    }

}
