package com.atkuzmanov.genesys;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.InvocationTargetException;

import static com.atkuzmanov.genesys.controllers.ResponseHeadersUtil.tracingResponseHeaders;

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
     * Handle failures commonly thrown from code
     */
    @ExceptionHandler({InvocationTargetException.class, IllegalArgumentException.class, ClassCastException.class,
            ConversionFailedException.class})
    @ResponseBody
    // TODO: WIP
    public ResponseEntity<?> handleMiscFailures(Throwable t) {
        return errorResponse(t, HttpStatus.BAD_REQUEST);
    }

    /**
     * Send a 409 Conflict in case of concurrent modification
     */
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockingFailureException.class,
            DataIntegrityViolationException.class})
    @ResponseBody
    public ResponseEntity<?> handleConflict(Exception ex) {
        return errorResponse(ex, HttpStatus.CONFLICT);
    }

    protected ResponseEntity<Object> errorResponse(Throwable throwable, HttpStatus status) {
        if (throwable != null) {
            // TODO: WIP
//            return response(new Exception(throwable), status);

            ResponseDetails responseDetails = ResponseDetails.builder()
                    .status(status.value())
                    .responseMessage(throwable.getMessage())
                    .throwable(new Exception(throwable))
                    .httpHeaders(tracingResponseHeaders())
                    .build();

            return response(responseDetails, status);
        } else {
            return response(null, status);
        }
    }

    protected <T> ResponseEntity<T> response(T body, HttpStatus status) {
        HttpHeaders h = new HttpHeaders();
        if(body instanceof ResponseDetails) {
            h = ((ResponseDetails) body).getHttpHeaders();
        }
        return new ResponseEntity<>(body, h, status);
    }
}
