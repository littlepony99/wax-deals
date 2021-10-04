package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.DiscogsBadRequestException;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.service.WantListService;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wantlist")
public class WantListController {

    private final WantListService wantListService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('USER')")
    public List<UniqueVinylDto> getUserWantList(@RequestAttribute(value = "userEntity", required = false) User user) {
        return wantListService.getWantListUniqueVinyls(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public void addVinylToUserWantList(@RequestAttribute(value = "userEntity", required = false) User user,
                                       @RequestBody UniqueVinylDto vinylDto) throws ForbiddenException, NotFoundException {
        wantListService.addWantedVinyl(user, vinylDto);
    }

    @PostMapping(path = "/import")
    @PreAuthorize("hasRole('USER')")
    public void importDiscogsWantList(@RequestAttribute(value = "userEntity", required = false) User user)
            throws DiscogsBadRequestException {
        wantListService.importWantList(user);
    }

}