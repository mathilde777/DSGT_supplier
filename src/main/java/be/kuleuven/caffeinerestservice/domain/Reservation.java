package be.kuleuven.caffeinerestservice.domain;

import java.time.Instant;

public class Reservation {
    private final String drinkId;
    private final Instant timestamp;
    private final String reservationId;
    private final String packageId;

    public Reservation(String drinkId, Instant timestamp, String resId, String packId) {
        this.drinkId = drinkId;
        this.timestamp = timestamp;
        this. reservationId = resId;
        this.packageId = packId;
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
    public String getPackageIdId() {
       return packageId;
    }
}
