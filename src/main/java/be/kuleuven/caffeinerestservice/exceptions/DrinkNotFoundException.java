package be.kuleuven.caffeinerestservice.exceptions;

public class DrinkNotFoundException extends RuntimeException {

    public DrinkNotFoundException(String id) {
        super("Could not find drink with ID " + id);
    }
}
