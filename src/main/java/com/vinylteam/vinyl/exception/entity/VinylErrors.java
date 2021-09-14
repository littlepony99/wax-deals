package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VinylErrors {

    NOT_FOUND_ERROR("Can't find such vinyl");

    private String message;

}
