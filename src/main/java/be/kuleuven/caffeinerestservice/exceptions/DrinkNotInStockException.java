package be.kuleuven.caffeinerestservice.exceptions;

public class DrinkNotInStockException extends RuntimeException {

    public DrinkNotInStockException(String id) {
        super("Drink with ID " + id + " is not in stock.");
    }
}
