package com.he194346.mvc.online_library_management_system.exception;

import com.he194346.mvc.online_library_management_system.enums.ErrorCode;

public class CustomException extends RuntimeException {

    private ErrorCode code;

    public CustomException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
