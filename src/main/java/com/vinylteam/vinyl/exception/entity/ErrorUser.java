package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorUser {

    INVALID_PASSWORD("Sorry, the password doesn't meet these requirements: minimum 8 symbols, uppercase, lowercase, number"),
    EMPTY_PASSWORD("Error. Password is empty. Please enter password correctly."),
    WRONG_PASSWORD("Password is not correct."),
    PASSWORDS_NOT_EQUAL("Passwords don't match!"),

    INVALID_EMAIL("Please, enter valid email address."),
    EMPTY_EMAIL("Error. Email is empty. Please enter email correctly."),
    EMAIL_NOT_FOUND_IN_DB("We can't find matching email. Please check your email or contact us."),
    EMAIL_NOT_VERIFIED("Email isn't verified yet, please, use the link sent to this email to confirm."),

    ADD_USER_EXISTING_EMAIL("We can't register user with these credentials, please, check them and try again."),
    ADD_USER_INVALID_VALUES("We can't register user with these credentials, please, check them and try again."),
    WRONG_CREDENTIALS("Email or password is not correct."),
    UPDATE_USER_EXISTING_EMAIL("We can't update account with this email, this user already exists."),
    UPDATE_USER_ERROR("Sorry, but account details couldn't be changed. Try again or contact support."),
    UPDATE_PASSWORD_ERROR("Sorry, but password couldn't be changed. Try again or contact support.");

    private String message;

}
