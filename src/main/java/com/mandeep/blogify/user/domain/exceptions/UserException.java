package com.mandeep.blogify.user.domain.exceptions;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final UserError userError;

    public UserException(UserError userError) {
        this.userError = userError;
    }

}
