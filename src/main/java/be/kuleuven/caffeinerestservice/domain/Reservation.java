package be.kuleuven.caffeinerestservice.domain;

import java.time.Instant;

public class Reservation {
    private final String drinkId;
    private final Instant timestamp;
    private final String reservationId;

    public Reservation(String drinkId, Instant timestamp, String resId) {
        this.drinkId = drinkId;
        this.timestamp = timestamp;
        this. reservationId = resId;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    public String getReservationId() {
        return reservationId;
    }
}
