package com.deployblitz.backend.exception;

import com.deployblitz.backend.utils.HttpResponse;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.nio.file.NoSuchFileException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpResponse<?> handleConflict(MethodArgumentNotValidException ex) {
        return new HttpResponse<>(
                HttpStatus.BAD_REQUEST,
                ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public HttpResponse<?> handleSQLIntegrityConstraintViolation(
            SQLIntegrityConstraintViolationException ex) {
        var message =
                switch (ex.getErrorCode()) {
                    case 1062 -> "Value Already Exist";
                    case 1063 -> "SYNTAX ERROR";
                    default -> ex.getMessage();
                };
        return new HttpResponse<>(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConnectException.class)
    public HttpResponse<?> handleConflict() {
        return new HttpResponse<>(HttpStatus.SERVICE_UNAVAILABLE, "Connection refused");
    }

    @ExceptionHandler(TransportException.class)
    public HttpResponse<?> handleConflict(TransportException ex) {
        return new HttpResponse<>(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(GitAPIException.class)
    public HttpResponse<?> handleConflict(GitAPIException ex) {
        return new HttpResponse<>(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public HttpResponse<?> handleConflict(FileNotFoundException ex) {
        return new HttpResponse<>(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoSuchFileException.class)
    public HttpResponse<?> handleConflict(NoSuchFileException ex) {
        log.info("No such file: {}", ex.getMessage());
        return new HttpResponse<>(HttpStatus.NOT_FOUND, "File or Directory Not Found '.deploy-blitz/blitz.sh'");
    }
}
