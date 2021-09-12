package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.impl.DefaultOneVinylOffersService;
import com.vinylteam.vinyl.web.dto.OneVinylPageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oneVinyl")
public class OneVinylOfferController {

    private final DefaultOneVinylOffersService oneVinylOffersService;

    @GetMapping("/{id}")
    public OneVinylPageDto getOneVinylOfferPage(@PathVariable("id") String id, HttpServletRequest request) {
        User user = (User) request.getAttribute("userEntity");
        Long userId = null;
        if (null != user) {
            userId = user.getId();
        }
        return oneVinylOffersService.prepareOneVinylInfo(id, userId);
    }

}
