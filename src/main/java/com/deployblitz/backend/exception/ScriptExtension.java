package com.deployblitz.backend.exception;

public class ScriptExtension extends RuntimeException {
    public ScriptExtension(String errorMessage) {
        super(errorMessage);
    }

    public ScriptExtension(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
