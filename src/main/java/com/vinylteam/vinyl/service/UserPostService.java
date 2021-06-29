package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UserPost;

public interface UserPostService {

    void processAdd(UserPost post);

    void add(UserPost post);

}