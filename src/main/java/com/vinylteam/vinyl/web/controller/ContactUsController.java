package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.web.dto.AddUserPostDto;
import com.vinylteam.vinyl.web.dto.CaptchaResponseDto;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.vinylteam.vinyl.util.ControllerResponseUtils.getStatusInfoMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/contact")
public class ContactUsController {
    private final UserPostService userPostService;

    @Value("${project.mail}")
    private String projectMail;

    @PostMapping
    public ResponseEntity<Map<String, Object>> contactUs(@RequestBody AddUserPostDto dto) throws ForbiddenException, ServerException {
        Map<String, Object> responseMap = new HashMap<>();
        userPostService.addUserPostWithCaptchaRequest(dto);
        responseMap.put("message", "Thank you. We will answer you as soon as possible.");
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseMap, HttpStatus.OK);
        log.debug("Set response status to {'status':{}}", HttpStatus.OK);
        return response;
    }

}
