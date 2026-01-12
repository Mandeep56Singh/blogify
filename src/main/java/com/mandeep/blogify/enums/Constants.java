package com.mandeep.blogify.enums;

import lombok.Getter;

@Getter
public enum Constants {
    MAX_PAGE_SIZE(50),
    ;
    private final int val;

    Constants(int val) {
        this.val = val;
    }
}
