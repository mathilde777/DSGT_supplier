package be.kuleuven.caffeinerestservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class DrinkNotInStockAdvice {

    @ResponseBody
    @ExceptionHandler(DrinkNotInStockException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String drinkNotInStockHandler(DrinkNotInStockException ex) {
        return ex.getMessage();
    }
}
