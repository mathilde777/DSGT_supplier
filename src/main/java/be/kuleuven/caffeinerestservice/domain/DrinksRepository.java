package be.kuleuven.caffeinerestservice.domain;

import be.kuleuven.caffeinerestservice.exceptions.DrinkNotFoundException;
import be.kuleuven.caffeinerestservice.exceptions.DrinkNotInStockException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.Instant;
import java.util.*;

@Component
public class DrinksRepository {
    private static final Map<String, Drink> drinks = new HashMap<>();
    private static final Map<String, Integer> stock = new HashMap<>();
    private static final Map<String, Reservation> reservedStock = new HashMap<>();

    @PostConstruct
    public void initData() {
        Drink n1 = new Drink();
        n1.setId("Ndjb0HZE6s3uxnAryOqA");
        n1.setName("Nalu Original");
        n1.setDescription("An amazing new taste of Nalu Drinks with less calories and a boost of energy.");
        n1.setBrand(Brand.NALU);
        n1.setVolume(250.0);
        n1.setKcal(50);
        n1.setPrice(1.08);


        drinks.put(n1.getId(), n1);
        stock.put(n1.getId(), 2);

        Drink n2 = new Drink();
        n2.setId("ep3FAfq9y06EYXx14Mt0");
        n2.setName("Nalu Passion");
        n2.setDescription("Introducing Nalu Passion, a refreshing, fruity blend of blueberry, pear, blackberry, peach, açai, Goji berries and mangosteen for the most passionate among us.");
        n2.setBrand(Brand.NALU);
        n2.setVolume(250.0);
        n2.setKcal(50);
        n2.setPrice(1.41);


        drinks.put(n2.getId(), n2);
        stock.put(n2.getId(), 1);

        Drink rb1 = new Drink();
        rb1.setId("eIRRKA8H6lUF19NWHtMZ");
        rb1.setName("Red Bull Green Edition");
        rb1.setDescription("The Red Bull Green Edition with the exotic flavor of cactus fruit.");
        rb1.setBrand(Brand.REDBULL);
        rb1.setVolume(250.0);
        rb1.setKcal(112);
        rb1.setPrice(1.65);


        drinks.put(rb1.getId(), rb1);
        stock.put(rb1.getId(), 5);
    }

    public Map<String, Integer> getStock() {
        releaseExpiredReservations();  // Check for expired reservations before returning stock
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            String id = entry.getKey();
            Integer quantity = entry.getValue();
            findDrink(id).ifPresent(drink -> result.put(drink.getName(), quantity));
        }
        return result;
    }

    public Map<String, Integer> findStock(String id) {
        Assert.notNull(id, "The id must not be null");
        releaseExpiredReservations();  // Check for expired reservations before returning stock for a specific item
        int quantity = stock.get(id);
        Drink d = findDrink(id).orElseThrow(() -> new DrinkNotFoundException(id));
        Map<String, Integer> result = new HashMap<>();
        result.put(d.getName(), quantity);
        return result;
    }

    public Collection<Drink> getDrinkOptions() {
        return drinks.values();
    }

    public Optional<Drink> findDrink(String id) {
        Assert.notNull(id, "The id must not be null");
        releaseExpiredReservations();  // Check for expired reservations before finding a drink
        Drink drink = drinks.get(id);
        return Optional.ofNullable(drink);
    }

    public boolean buyDrink(String reservationId) {
        Assert.notNull(reservationId, "The reservationId must not be null");
        releaseExpiredReservations();  // Check for expired reservations before buying a drink

        List<String> drinkIds = new ArrayList<>();

        // Find all reservations with the given reservationId
        for (Map.Entry<String, Reservation> entry : reservedStock.entrySet()) {
            if (entry.getValue().getReservationId().equals(reservationId)) {
                drinkIds.add(entry.getKey());
            }
        }

        if (drinkIds.isEmpty()) {
            throw new DrinkNotFoundException(reservationId);
        }

        boolean allPurchased = true;

        // Check if the drinks are in stock and decrement stock
        for (String drinkId : drinkIds) {
            if (stock.containsKey(drinkId) && stock.get(drinkId) > 0) {
                int newStock = stock.get(drinkId) - 1;
                stock.put(drinkId, newStock);

                // Remove the reservation since the drink is purchased
                reservedStock.remove(drinkId);
            } else {
                allPurchased = false;
            }
        }

        if (!allPurchased) {
            throw new DrinkNotInStockException("One or more drinks with reservation ID " + reservationId + " are not in stock.");
        }

        return true;
    }


    public boolean reserveDrink(String id , String resId) {

        System.out.print("testtt");
        Assert.notNull(id, "The id must not be null");
        releaseExpiredReservations();  // Check for expired reservations before reserving a drink
        //Drink d = findDrink(id).orElseThrow(() -> new DrinkNotFoundException(id))
        if (stock.containsKey(id) && stock.get(id) > 0) {
            int newStock = stock.get(id) - 1;
            stock.put(id, newStock);
           // d.setReserved(d.getReserved() + 1);
            reservedStock.put(id, new Reservation(id, Instant.now(),resId));
            return true;
        } else {
           // throw new DrinkNotInStockException(id);
            return false;
        }
    }

    private void releaseExpiredReservations() {
        Instant now = Instant.now();
        List<String> expiredReservations = new ArrayList<>();

        // Identify expired reservations
        for (Map.Entry<String, Reservation> entry : reservedStock.entrySet()) {
            if (entry.getValue().getTimestamp().plusSeconds(2 * 60).isBefore(now)) {
                expiredReservations.add(entry.getKey());
            }
        }
        for (String id : expiredReservations) {
            reservedStock.remove(id);
            Drink d = findDrink(id).orElseThrow(() -> new DrinkNotFoundException(id));
            stock.put(id, stock.get(id) + 1);
        }
    }

    public boolean checkReservation(String reservationId, String id) {
        Assert.notNull(reservationId, "The reservationId must not be null");
        Assert.notNull(id, "The id must not be null");
        releaseExpiredReservations();  // Check for expired reservations before checking

        // Retrieve the reservation based on the drink id
        Reservation reservation = reservedStock.get(id);

        // Check if the retrieved reservation matches the provided reservationId
        return reservation != null && reservation.getReservationId().equals(reservationId);
    }

    public boolean checkAvailability(String id) {
        Assert.notNull(id, "The id must not be null");
        releaseExpiredReservations();  // Check for expired reservations before checking availability
        return stock.containsKey(id) && stock.get(id) > 0;
    }


    public void getReserved() {
        releaseExpiredReservations();  // Check for expired reservations before returning stock
        Map<String, Reservation> result = new HashMap<>();
        for (Map.Entry<String, Reservation> entry : reservedStock.entrySet()) {

            System.out.print(" Reserved " + entry.getKey() + "Reservation" + entry.getValue().getReservationId());
        }

    }

}
