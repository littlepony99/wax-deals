package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.exception.entity.UserPostErrors;
import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.AddUserPostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultUserPostService implements UserPostService {
    private final static String CONTACT_US_DEFAULT_THEME = "Mail from customer";

    private final UserPostDao userPostDao;
    private final MailSender mailSender;
    private final CaptchaService captchaService;

    @Value("${project.mail}")
    private String projectMail;

    @Override
    @Transactional
    public void processAdd(UserPost post) throws ServerException {
        userPostDao.add(post);
        String mailMessage = createContactUsMessage(post.getEmail(), post.getMessage());
        mailSender.sendMail(projectMail, CONTACT_US_DEFAULT_THEME, mailMessage);
    }

    @Override
    @Transactional
    public void addUserPostWithCaptchaRequest(AddUserPostDto dto) throws ForbiddenException, ServerException {
        boolean isCaptchaValid = captchaService.validateCaptcha(dto.getRecaptchaToken());

        if (!isCaptchaValid) {
            throw new ForbiddenException(UserPostErrors.INCORRECT_CAPTCHA_ERROR.getMessage());
        }

        UserPost post = UserPost.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .message(dto.getContactUsMessage())
                .createdAt(LocalDateTime.now())
                .build();
        try {
            processAdd(post);
            log.info("Post added");
        } catch (ServerException e) {
            log.info("Post not added");
            throw e;
        }
    }

    String createContactUsMessage(String recipient, String mailBody) {
        return "MailFrom: " + recipient + System.lineSeparator() + "Message: " + mailBody;
    }
}
