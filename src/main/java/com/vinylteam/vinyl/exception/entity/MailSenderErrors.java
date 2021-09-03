package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MailSenderErrors {

    FAILED_TO_SEND_EMAIL("Sorry, we weren't able to handle your request. Please contact us via social media.");

    private String message;

}
