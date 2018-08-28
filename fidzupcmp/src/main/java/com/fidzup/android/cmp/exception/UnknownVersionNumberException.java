package com.fidzup.android.cmp.exception;

import android.os.Build;
import android.support.annotation.RequiresApi;

@SuppressWarnings("unused")
public class UnknownVersionNumberException extends Exception {
    public UnknownVersionNumberException() {
    }

    public UnknownVersionNumberException(String message) {
        super(message);
    }

    public UnknownVersionNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownVersionNumberException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public UnknownVersionNumberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
