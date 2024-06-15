package be.kuleuven.caffeinerestservice.domain;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();

    public void addReservation(String snackId, String reservationId) {
        Reservation reservation = new Reservation(reservationId, snackId, Instant.now());
        reservations.add(reservation);
    }

    public boolean buyReservation(String reservationId) {
        Iterator<Reservation> iterator = reservations.iterator();
        while (iterator.hasNext()) {
            Reservation reservation = iterator.next();
            if (reservation.getReservationId().equals(reservationId)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public void releaseExpiredReservations(DrinksRepository drinkRepository) {
        Instant now = Instant.now();
        Iterator<Reservation> iterator = reservations.iterator();
        while (iterator.hasNext()) {
            Reservation reservation = iterator.next();
            if (reservation.getTimestamp().plusSeconds(5 * 60).isBefore(now)) {
                drinkRepository.releaseReservation(reservation.getDrinkId());
                iterator.remove();
            }
        }
    }
    public List<Reservation> getReservations() {
        return reservations;
    }
}
