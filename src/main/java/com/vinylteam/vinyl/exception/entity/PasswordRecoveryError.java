package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PasswordRecoveryError {

    EMPTY_PASSWORD("Sorry, the password is empty!"),
    PASSWORDS_NOT_EQUAL("Sorry, the passwords don't match!"),

    EMPTY_EMAIL("Error. Email is empty. Please enter email correctly."),
    EMAIL_NOT_FOUND_IN_DB("We can't find matching email. Please check your email or contact us."),

    TOKEN_NOT_CORRECT_UUID("Your link is incorrect! Please check the link in the your email"),
    TOKEN_NOT_FOUND_IN_DB("Your link is incorrect! Please check the link in the your email or contact support."),
    DELETE_TOKEN_ERROR("Sorry, problem with processing this link. Please, request new one or contact support."),
    TOKEN_IS_EXPIRED("The link you are trying to follow is no longer valid. You need to" +
            " repeat the recovery password process."),

    ADD_TOKEN_ERROR("We can't recover your password by this email. Please check your email or contact us."),
    UPDATE_PASSWORD_ERROR("Sorry, but password couldn't be changed. Try again or contact support."),
    EMAIL_SEND_ERROR("Something went wrong. Please contact support.");

    private String message;

}
