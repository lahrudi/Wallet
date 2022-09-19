package org.leovegas.wallet.exception;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;

/**
 * @author Alireza Gholamzadeh Lahroodi
 */

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(value={  IllegalArgumentException.class })
    public ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(IllegalArgumentException ex, WebRequest request) {
        log.warn(ex.getMessage(), ex);

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false),
                LocalDate.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( value={ Exception.class })
    public ResponseEntity<ErrorResponse> commonExceptionHandler(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getDescription(false),
                LocalDate.now()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                           @NotNull HttpHeaders headers, HttpStatus status,
                                                                           @NotNull WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(ex.getMessage());

        return handleExceptionInternal(ex, errorMessage,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value={ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false),
                LocalDate.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={  DataIntegrityViolationException.class })
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException (
            DataIntegrityViolationException  ex, WebRequest request) {

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getDescription(false),
                LocalDate.now()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value
            = { WalletException.class })
    protected ResponseEntity<ErrorResponse> handleWalletException(
            WalletException ex, WebRequest request) {
        logger.error(ex.toString());
        HttpStatus status = HttpStatus.valueOf(ex.getErrorCode());

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<>(new ErrorResponse(
                status,
                ex.getMessage(),
                request.getDescription(false),
                LocalDate.now()),
                status);

    }



}
