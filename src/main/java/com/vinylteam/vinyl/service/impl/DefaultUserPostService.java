package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.util.MailSender;

public class DefaultUserPostService implements UserPostService {

    private final UserPostDao userPostDao;
    private final MailSender mailSender;

    public DefaultUserPostService(UserPostDao userPostDao, MailSender mailSender) {
        this.userPostDao = userPostDao;
        this.mailSender = mailSender;
    }

    @Override
    public boolean processAdd(UserPost post) {
        boolean isAddedToDb = userPostDao.add(post);
        boolean isMailSent = mailSender.sendMail(post.getEmail(), post.getTheme(), post.getMessage());
        return isAddedToDb && isMailSent;
    }

    @Override
    public boolean add(UserPost post) {
        return userPostDao.add(post);
    }

}