package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmailConfirmationErrors {

    TOKEN_FROM_LINK_NOT_FOUND("Your link isn't correct. Please, try again or contact us"),
    TOKEN_FROM_LINK_NOT_UUID("Your link isn't correct. Please, try again or contact us"),
    TOKEN_EXPIRED("Sorry, this link expired. We sent you a new one!"),

    EMPTY_EMAIL("Error. Email to confirm is empty. Please, contact us."),
    TOKEN_FOR_USER_ID_NOT_FOUND("Sorry we can't find the link for this user"),
    CAN_NOT_ADD_LINK_FOR_EMAIL("Sorry, can't create link to confirm that email."),
    CAN_NOT_CREATE_LINK_TRY_AGAIN("Sorry, error on our side, could you try one more time? Contact us if it doesn't work"),
    CAN_NOT_SEND_EMAIL("Sorry, we couldn't send you a link to confirm your email. Please, try again or contact us.");

    private String message;

}
