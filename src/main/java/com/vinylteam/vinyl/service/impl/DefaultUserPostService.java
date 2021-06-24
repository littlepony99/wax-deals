package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.util.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DefaultUserPostService implements UserPostService {

    @Value("${project.mail}")
    String projectMail;
    private final static String CONTACT_US_DEFAULT_THEME = "Mail from customer";
    private final UserPostDao userPostDao;
    private final MailSender mailSender;

    @Override
    @Transactional
    public void processAdd(UserPost post) {
        userPostDao.add(post);
        String mailMessage = createContactUsMessage(post.getEmail(), post.getTheme(), post.getMessage());
        boolean isMailSent = mailSender.sendMail(projectMail, CONTACT_US_DEFAULT_THEME, mailMessage);
    }

    @Override
    public void add(UserPost post) {
        userPostDao.add(post);
    }

    protected String createContactUsMessage(String recipient, String subject, String mailBody) {
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
