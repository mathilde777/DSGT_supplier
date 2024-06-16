package be.kuleuven.caffeinerestservice.controllers;

import be.kuleuven.caffeinerestservice.domain.Drink;
import be.kuleuven.caffeinerestservice.service.DrinkService;
import be.kuleuven.caffeinerestservice.exceptions.CodeNotCorrectException;
import be.kuleuven.caffeinerestservice.exceptions.DrinkNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/rest")
public class DrinksRestController {

    private final DrinkService drinkService;

    @Autowired
    DrinksRestController (DrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping("/items/{code}")
    public CollectionModel<EntityModel<Drink>> getAll(@PathVariable String code) {
        if (checkCode(code)) {
            List<Drink> snacks = drinkService.getSnackOptions().stream().toList();
            List<EntityModel<Drink>> snackEntityModels = snacks.stream()
                    .map(snack -> snackToEntityModel(snack.getId(), snack, code))
                    .toList();
            return CollectionModel.of(snackEntityModels,
                    linkTo(methodOn(DrinksRestController.class).getAll(code)).withSelfRel());
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @GetMapping("/itemId/{id}/{code}")
    public EntityModel<Drink> getItem(@PathVariable String id, @PathVariable String code) {
        if (checkCode(code)) {
            Drink snack = drinkService.findSnack(id).orElseThrow(() -> new DrinkNotFoundException(id));
            return snackToEntityModel(id, snack, code);
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/stock/{code}")
    public ResponseEntity<String> getAllStock(@PathVariable String code) {
        if (checkCode(code)) {
            drinkService.getStock();
            return ResponseEntity.ok("Stock have been printed to the console.");
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/itemID/{id}/stock/{code}")
    public EntityModel<Map<String, Integer>> getStock(@PathVariable String id, @PathVariable String code) {
        if (checkCode(code)) {
            Map<String, Integer> stock = drinkService.findStock(id);
            return EntityModel.of(stock,
                    linkTo(methodOn(DrinksRestController.class).getStock(id, code)).withSelfRel());
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/itemId/{id}/reserve/{reservationId}/{code}")
    public ResponseEntity<String> reserveSnack(@PathVariable String id, @PathVariable String reservationId, @PathVariable String code) {
        if (checkCode(code)) {
            boolean success = drinkService.reserveSnack(id, reservationId);
            if (success) {
                Map<String, Integer> stock = drinkService.findStock(id); // Get the updated stock
                String message = "Snack with ID " + id + " reserved successfully. " + stock.values() + " left in stock.";
                return ResponseEntity.ok(message);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to reserve snack with ID " + id);
            }
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/buy/{reservationId}/{code}")
    public ResponseEntity<String> buySnack(@PathVariable String reservationId, @PathVariable String code) {
        if (checkCode(code)) {
            boolean success = drinkService.buyReservation(reservationId);
            if (success) {
                return ResponseEntity.ok("Snack with reservation ID " + reservationId + " purchased successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to purchase snack with reservation ID " + reservationId);
            }
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/itemId/{id}/checkReservation/{reservationId}/{code}")
    public ResponseEntity<Boolean> checkReservation(@PathVariable String id, @PathVariable String reservationId, @PathVariable String code) {
        if (checkCode(code)) {
            boolean exists = drinkService.checkReservation(reservationId, id);
            return ResponseEntity.ok(exists);
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/releaseReservation/{reservationId}/{code}")
    public void releaseReservation(@PathVariable String reservationId, @PathVariable String code) {
        if (checkCode(code)) {
            drinkService.releaseReservation(reservationId);
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/itemId/{id}/checkAvailability/{code}")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable String id, @PathVariable String code) {
        if (checkCode(code)) {
            boolean available = drinkService.checkAvailability(id);
            return ResponseEntity.ok(available);
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/getReserved/{code}")
    public ResponseEntity<String> getReserved(@PathVariable String code) {
        if (checkCode(code)) {
            drinkService.getReserved();
            return ResponseEntity.ok("Reserved snacks have been printed to the console.");
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    private boolean checkCode(String code) {
        String verificationCode = "1234";
        return code.equals(verificationCode);
    }

    private EntityModel<Drink> snackToEntityModel(String id, Drink snack, String code) {
        return EntityModel.of(snack,
                linkTo(methodOn(DrinksRestController.class).getAll(code)).withRel("rest/snacks"),
                linkTo(methodOn(DrinksRestController.class).getItem(id, code)).withRel("rest/snackId/{id}"));
    }
}
