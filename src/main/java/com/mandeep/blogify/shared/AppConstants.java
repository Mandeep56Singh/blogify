package com.mandeep.blogify.shared;

public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    public static final Integer MAX_PAGE_SIZE = 50;
    public static final String EMAIL_REGREX = "^(?=.{1,64}@)" +
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
            "@" +
            "[a-z0-9]([a-z0-9-]*[a-z0-9])?(\\.[a-z0-9]([a-z0-9-]*[a-z0-9])?)*" +
            "\\.[a-z]{2,}$";
    public static final int USER_NAME_MIN_LENGTH = 3;
    public static final int USER_NAME_MAX_LENGTH = 30;
}
