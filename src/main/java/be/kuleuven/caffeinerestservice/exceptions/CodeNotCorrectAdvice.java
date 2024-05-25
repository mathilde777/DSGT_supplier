package be.kuleuven.caffeinerestservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class CodeNotCorrectAdvice {

    @ResponseBody
    @ExceptionHandler(CodeNotCorrectException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String codeNotCorrectHandler(CodeNotCorrectException ex) {
        return ex.getMessage();
    }
}
