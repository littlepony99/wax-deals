package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.util.MailSender;

import java.util.UUID;

public class DefaultUserPostService implements UserPostService {

    private final static String PROJECT_MAIL = "waxdealsproject@gmail.com";
    private final static String CONTACT_US_DEFAULT_THEME = "Mail from customer";
    private final UserPostDao userPostDao;
    private final MailSender mailSender;

    public DefaultUserPostService(UserPostDao userPostDao, MailSender mailSender) {
        this.userPostDao = userPostDao;
        this.mailSender = mailSender;
    }

    @Override
    public boolean processAdd(UserPost post) {
        boolean isAddedToDb = userPostDao.add(post);
        String mailMessage = createContactUsMessage(post.getEmail(), post.getTheme(), post.getMessage());
        boolean isMailSent = mailSender.sendMail(PROJECT_MAIL, CONTACT_US_DEFAULT_THEME, mailMessage);
        return isAddedToDb && isMailSent;
    }

    @Override
    public boolean add(UserPost post) {
        return userPostDao.add(post);
    }

    @Override
    public String addRecoveryUserToken(long userId){
        boolean isAdded;
        String recoveryToken = UUID.randomUUID().toString();
        if (userPostDao.getRecoveryUserToken(userId).isEmpty()){
            isAdded = userPostDao.addRecoveryUserToken(userId, recoveryToken);
        } else {
            isAdded = userPostDao.updateRecoveryUserToken(userId, recoveryToken);
        }
        if (!isAdded){
            recoveryToken="";
        }
        return recoveryToken;
    }

    @Override
    public long getRecoveryUserId(String token){
        return userPostDao.getRecoveryUserId(token);
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