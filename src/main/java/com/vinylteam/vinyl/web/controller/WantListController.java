package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.service.WantListService;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/wantlist")
public class WantListController {

    private final WantListService wantListService;


    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('USER')")
    public List<UniqueVinylDto> getUserWantList(HttpServletRequest request) {
        User user = (User) request.getAttribute("userEntity");
        return wantListService.getWantListUniqueVinyls(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UniqueVinylDto> addVinylToUserWantList(HttpServletRequest request,
                                                                 @RequestBody UniqueVinylDto vinylDto) throws ForbiddenException {
        User user = (User) request.getAttribute("userEntity");
        wantListService.addWantedVinyl(user, vinylDto);
        return new ResponseEntity<>(OK);
    }

    @PostMapping(path = "/import")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ChangePasswordResponse> importDiscogsWantList(HttpServletRequest request) {
        User user = (User) request.getAttribute("userEntity");
        wantListService.importWantList(user);
        return new ResponseEntity<>(OK);
    }

}