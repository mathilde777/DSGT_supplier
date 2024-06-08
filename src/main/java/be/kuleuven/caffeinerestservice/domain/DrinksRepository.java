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
    private static final List<Reservation> reservations = new ArrayList<>();

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
        System.out.println("STOCK: " + quantity);
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

    public boolean buy(String reservationId) {
        Assert.notNull(reservationId, "The reservationId must not be null");
        releaseExpiredReservations();  // Check for expired reservations before buying a drink
        boolean result = false;
        Iterator<Reservation> iterator = reservations.iterator();
        while (iterator.hasNext()) {
            Reservation reservation = iterator.next();
            if (reservation.getReservationId().equals(reservationId)) {
                iterator.remove();
                result = true;
            }
        }
        return result;
       // throw new DrinkNotFoundException("No reservations found with reservation ID " + reservationId);

    }

    public boolean reserve(String id, String resId) {
        Assert.notNull(id, "The id must not be null");
        releaseExpiredReservations();  // Check for expired reservations before reserving a drink
        if (stock.containsKey(id) && stock.get(id) > 0) {
            int newStock = stock.get(id) - 1;
            stock.put(id, newStock);
            Reservation reservation = new Reservation(id, Instant.now(), resId);
            reservations.add(reservation);
            return true;
        } else {
            throw new DrinkNotInStockException(id);
        }
    }

    private void releaseExpiredReservations() {
        Instant now = Instant.now();
        Iterator<Reservation> iterator = reservations.iterator();
        while (iterator.hasNext()) {
            Reservation reservation = iterator.next();
            if (reservation.getTimestamp().plusSeconds(5 * 60).isBefore(now)) {
                iterator.remove();
                stock.put(reservation.getDrinkId(), stock.getOrDefault(reservation.getDrinkId(), 0) + 1);
            }
        }
    }

    public boolean checkReservation(String reservationId, String id) {
        Assert.notNull(reservationId, "The reservationId must not be null");
        Assert.notNull(id, "The id must not be null");
        releaseExpiredReservations();  // Check for expired reservations before checking

        for (Reservation res : reservations) {
            if (res.getReservationId().equals(reservationId) && res.getDrinkId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkAvailability(String id) {
        Assert.notNull(id, "The id must not be null");
        releaseExpiredReservations();  // Check for expired reservations before checking availability
        return stock.containsKey(id) && stock.get(id) > 0;
    }

    public void getReserved() {
        releaseExpiredReservations();  // Check for expired reservations before returning reserved stock

        System.out.println("RESERVED STOCK:");
        for (Reservation reservation : reservations) {
            System.out.println("Reserved " + reservation.getDrinkId() + " | Reservation ID: " + reservation.getReservationId());
        }
    }

}
