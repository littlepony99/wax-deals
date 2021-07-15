package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.CaptchaRequestDto;
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
    public void processAdd(UserPost post) {
        userPostDao.add(post);
        String mailMessage = createContactUsMessage(post.getEmail(), post.getTheme(), post.getMessage());
        mailSender.sendMail(projectMail, CONTACT_US_DEFAULT_THEME, mailMessage);
        // expectedDataSet check that nothing was add to userPost table
        //check that methods  getEmail, getTheme will invoked
    }

    @Override
    public Boolean processRequest(CaptchaRequestDto dto) throws ForbiddenException {
        boolean isCaptchaValid = captchaService.validateCaptcha(dto.getCaptchaResponse());

        if (isCaptchaValid) {
            UserPost post = UserPost.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .theme(dto.getSubject())
                    .message(dto.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();
            try {
                processAdd(post);
                log.info("Post added");
                return Boolean.TRUE;
            } catch (RuntimeException e) {
                log.info("Post not added");
                return Boolean.FALSE;
            }
        } else {
            throw new ForbiddenException("INVALID_CAPTCHA");
        }
    }

    String createContactUsMessage(String recipient, String subject, String mailBody) {
        return new StringBuilder()
                .append("MailFrom: ")
                .append(recipient)
                .append(System.lineSeparator())
                .append("Theme: ")
                .append(subject)
                .append(System.lineSeparator())
                .append("Message: ")
                .append(mailBody).toString();
    }
}
