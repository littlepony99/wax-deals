package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.service.impl.DefaultCatalogService;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    private final DefaultCatalogService catalogService;

    private final int amountOfRandomVinyls = 50;

    @GetMapping
    public List<UniqueVinylDto> getCatalogPage() {
        return catalogService.findRandomUniqueVinyls(amountOfRandomVinyls);
    }

    @GetMapping("/{id}")
    public OneVinylPageDto getOneVinylOfferPage(@RequestAttribute(value = "userEntity", required = false) User user,
                                                @PathVariable("id") String id) throws NotFoundException {
        Long userId = user == null ? null : user.getId();
        return catalogService.getOneVinylPageDto(id, userId);
    }

}
