package be.kuleuven.caffeinerestservice.domain;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DrinksRepository {
    private static final Map<String, Drink> drinks = new HashMap<>();
    private static final Map<String, Integer> stock = new HashMap<>();

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
        return new HashMap<>(stock);
    }

    public Map<String, Integer> findStock(String id) {
        int quantity = stock.getOrDefault(id, 0);
        Drink drink = findDrink(id).orElseThrow(() -> new RuntimeException("Snack not found"));
        Map<String, Integer> result = new HashMap<>();
        result.put(drink.getName(), quantity);
        return result;
    }

    public Collection<Drink> getDrinkOptions() {
        return drinks.values();
    }

    public Optional<Drink> findDrink(String id) {
        return Optional.ofNullable(drinks.get(id));
    }

    public boolean reserveDrink(String id, String reservationId) {
        if (stock.containsKey(id) && stock.get(id) > 0) {
            stock.put(id, stock.get(id) - 1);
            return true;
        } else {
            throw new RuntimeException("Drink not in stock");
        }
    }

    public void releaseReservation(String id) {
        stock.put(id, stock.getOrDefault(id, 0) + 1);
    }
}
