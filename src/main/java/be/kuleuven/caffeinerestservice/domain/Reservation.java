package be.kuleuven.caffeinerestservice.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class Reservation {

    @Id
    private String reservationId;
    private String drinkId;
    private Instant timestamp;

    // Constructors
    public Reservation() {}

    public Reservation(String reservationId, String drinkId, Instant timestamp) {
        this.reservationId = reservationId;
        this.drinkId = drinkId;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setSnackId(String snackId) {
        this.drinkId = snackId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
