package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserErrors {

    INVALID_PASSWORD_ERROR("Sorry, the password doesn't meet these requirements: minimum 8 symbols, uppercase, lowercase, number"),
    EMPTY_PASSWORD_ERROR("Error. Password is empty. Please enter password correctly."),
    WRONG_PASSWORD_ERROR("Password is not correct."),
    PASSWORDS_NOT_EQUAL_ERROR("Passwords don't match!"),

    INVALID_EMAIL_ERROR("Please, enter valid email address."),
    EMPTY_EMAIL_ERROR("Error. Email is empty. Please enter email correctly."),
    EMAIL_NOT_FOUND_IN_DB_ERROR("We can't find matching email. Please check your email or contact us."),
    EMAIL_NOT_VERIFIED_ERROR("Email isn't verified yet, please, use the link sent to this email to confirm."),

    ADD_USER_EXISTING_EMAIL_ERROR("We can't register user with these credentials, please, check them and try again."),
    ADD_USER_INVALID_VALUES_ERROR("We can't register user with these credentials, please, check them and try again."),
    WRONG_CREDENTIALS_ERROR("Email or password is not correct."),
    UPDATE_USER_EXISTING_EMAIL_ERROR("We can't update account with this email, this user already exists."),
    UPDATE_USER_ERROR("Sorry, but account details couldn't be changed. Try again or contact support."),
    UPDATE_PASSWORD_ERROR("Sorry, but password couldn't be changed. Try again or contact support.");

    private String message;

}
