package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CatalogErrors {

    VINYL_BY_ID_NOT_FOUND("Sorry, we don't have vinyl with this id.");

    private String message;


}
