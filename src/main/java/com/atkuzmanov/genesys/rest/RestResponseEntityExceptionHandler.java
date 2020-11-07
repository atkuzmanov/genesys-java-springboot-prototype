package com.atkuzmanov.genesys.rest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.InvocationTargetException;

import static com.atkuzmanov.genesys.controllers.ResponseHeadersUtil.tracingResponseHeaders;

/**
 * RE: Logging REST Exceptions with Spring
 * - <https://objectpartners.com/2014/10/21/logging-rest-exceptions-with-spring/>
 *
 * RE: Difficulty in missing errors
 * RE: Spring-Boot: Missing logs on HTTP 500
 * > If you don't handle the exception at all then it will become an error dispatch, i.e. filters are executed a second time with the DispatcherType.ERROR trying to delegate to an error page. By default you don't get any from Spring, iirc.
 * >
 * >  Error dispatch is a tricky beast to get right, especially with Logbook. See #32, #155 and #334. Past experiences can be best summarized as Don't use ERROR dispatch.
 * >
 * >  Instead I'd suggest to use custom @ExceptionHandlers or something like https://github.com/zalando/problem-spring-web.
 * - <https://github.com/zalando/logbook/issues/488>
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Catch all for any other exceptions...
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity<?> handleAnyException(Exception e) {
        return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle failures commonly thrown from code.
     */
    @ExceptionHandler({InvocationTargetException.class, IllegalArgumentException.class, ClassCastException.class,
            ConversionFailedException.class})
    @ResponseBody
    public ResponseEntity<?> handleMiscFailures(Throwable t) {
        return errorResponse(t, HttpStatus.BAD_REQUEST);
    }

    /**
     * Send a 409 Conflict in case of concurrent modification.
     */
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class,
            DataIntegrityViolationException.class})
    @ResponseBody
    public ResponseEntity<?> handleConflict(Exception ex) {
        return errorResponse(ex, HttpStatus.CONFLICT);
    }

    protected ResponseEntity<?> errorResponse(Throwable throwable, HttpStatus status) {
        if (throwable != null) {
            return ResponseDetails.builder()
                    .status(status.value())
                    .responseMessage(throwable.getMessage())
                    .throwable(new Exception(throwable))
                    .headers(tracingResponseHeaders())
                    .entity();
        } else {
            return response(null, status);
        }
    }

    protected <T> ResponseEntity<T> response(T body, HttpStatus status) {
        return new ResponseEntity<>(body, tracingResponseHeaders(), status);
    }
}
