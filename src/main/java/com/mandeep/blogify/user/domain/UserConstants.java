package com.mandeep.blogify.user.domain;

import lombok.Getter;

@Getter
public enum UserConstants {
    USER_NAME_MIN_LENGTH(3),
    USER_NAME_MAX_LENGTH(30),
    ;

    private final int value;

    UserConstants(int value) {
        this.value = value;
    }

}