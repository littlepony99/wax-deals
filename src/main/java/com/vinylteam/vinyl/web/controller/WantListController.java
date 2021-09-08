package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.ChangePasswordResponse;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/wantlist")
public class WantListController {
    private final UniqueVinylService uniqueVinylService;
    private final UniqueVinylMapper uniqueVinylMapper;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('USER')")
    public List<UniqueVinylDto> getUserWantListWantList(HttpServletRequest request) {
        List<UniqueVinyl> uniqueVinyls = uniqueVinylService.findRandom(10);
        return uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(uniqueVinyls);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UniqueVinylDto> addVinylToUserWantList(HttpServletRequest request, @RequestBody UniqueVinylDto vinylDto) {
        return new ResponseEntity<>(OK);
    }

    @PostMapping(path = "/import")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChangePasswordResponse> changePassword(HttpServletRequest request) {
        return new ResponseEntity<>(OK);
    }

}
