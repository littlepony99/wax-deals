package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.web.dto.ChangePasswordResponse;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/wantlist")
public class WantListController {

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UniqueVinylDto> getUserWantListWantList(HttpServletRequest request) {
        return new ResponseEntity<>(OK);
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
