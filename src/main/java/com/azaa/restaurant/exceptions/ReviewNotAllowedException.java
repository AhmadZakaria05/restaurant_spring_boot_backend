package com.azaa.restaurant.exceptions;

public class ReviewNotAllowedException extends BaseException{

    public ReviewNotAllowedException(String message) {
        super(message);
    }

    public ReviewNotAllowedException() {
    }

    public ReviewNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReviewNotAllowedException(Throwable cause) {
        super(cause);
    }
}
