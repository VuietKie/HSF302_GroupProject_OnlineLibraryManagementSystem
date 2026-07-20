package com.he194346.mvc.online_library_management_system.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT),
    AUTHOR_NOT_FOUND(HttpStatus.NOT_FOUND),
    AUTHOR_ALREADY_EXISTS(HttpStatus.CONFLICT),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND),
    BOOK_ALREADY_EXISTS(HttpStatus.CONFLICT),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_RESERVATION(HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BOOK_COPIES(HttpStatus.CONFLICT),
    BORROW_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_BORROW_RECORD(HttpStatus.BAD_REQUEST),
    BOOK_SELF_EDIT_FORBIDDEN(HttpStatus.FORBIDDEN);

    private final HttpStatus httpStatus;

    ErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
