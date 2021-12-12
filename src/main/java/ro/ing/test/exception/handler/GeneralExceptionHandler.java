package ro.ing.test.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ro.ing.test.dto.response.ErrorResponse;
import ro.ing.test.exception.NotFoundException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    protected ErrorResponse handle(NotFoundException ex) {
        log.warn("Not found element", ex);
        return new ErrorResponse(NOT_FOUND.value(), "----"+ex.getMessage());
    }
}
