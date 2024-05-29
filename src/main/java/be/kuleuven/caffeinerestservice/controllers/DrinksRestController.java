package be.kuleuven.caffeinerestservice.controllers;

import be.kuleuven.caffeinerestservice.domain.Drink;
import be.kuleuven.caffeinerestservice.domain.DrinksRepository;
import be.kuleuven.caffeinerestservice.exceptions.CodeNotCorrectException;
import be.kuleuven.caffeinerestservice.exceptions.DrinkNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DrinksRestController {

    private final DrinksRepository drinksRepository;

    @Autowired
    DrinksRestController(DrinksRepository drinksRepository) {
        this.drinksRepository = drinksRepository;
    }

    @GetMapping("/rest/drinks/{code}")
    CollectionModel<EntityModel<Drink>> getAllDrinks(@PathVariable String code) {
        if (checkCode(code)) {
            Collection<Drink> drinks = drinksRepository.getDrinkOptions();

            List<EntityModel<Drink>> drinkEntityModels = new ArrayList<>();
            for (Drink d : drinks) {
                EntityModel<Drink> em = drinkToEntityModel(d.getId(), d, code);
                drinkEntityModels.add(em);
            }
            return CollectionModel.of(drinkEntityModels,
                    linkTo(methodOn(DrinksRestController.class).getAllDrinks(code)).withSelfRel());
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    private boolean checkCode(String code) {
        System.out.println(code);
        String verificationCode = "1234";
        return code.equals(verificationCode);
    }

    @GetMapping("rest/drinksId/{id}/{code}")
    EntityModel<Drink> getDrink(@PathVariable String id, @PathVariable String code) {
        if (checkCode(code)) {
            Drink d = drinksRepository.findDrink(id).orElseThrow(() -> new DrinkNotFoundException(id));
            return drinkToEntityModel(id, d, code);
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @GetMapping("/rest/stock/{code}")
    public EntityModel<Map<String, Integer>> getAllStock(@PathVariable String code) {
        if (checkCode(code)) {
            Map<String, Integer> stock = drinksRepository.getStock();
            return EntityModel.of(stock,
                    linkTo(methodOn(DrinksRestController.class).getAllStock(code)).withSelfRel());
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/rest/stockID/{id}/{code}")
    public EntityModel<Map<String, Integer>> getStock(@PathVariable String id, @PathVariable String code) {
        if (checkCode(code)) {
            Map<String, Integer> stock = drinksRepository.findStock(id);
            return EntityModel.of(stock,
                    linkTo(methodOn(DrinksRestController.class).getStock(id, code)).withSelfRel());
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/rest/drinksId/{id}/reserve/{reservationId}/{packageId}/{code}")
    public ResponseEntity<String> reserveDrink(@PathVariable String id, @PathVariable String reservationId, @PathVariable String packageId, @PathVariable String code) {
        if (checkCode(code)) {
            boolean success = drinksRepository.reserveDrink(id, reservationId, packageId);
            if (success) {
                Map<String, Integer> stock = drinksRepository.findStock(id); // Get the updated stock
                String message = "Drink with ID " + id + " reserved successfully. " + stock.values() + " left in stock.";
                return ResponseEntity.ok(message);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to reserve drink with ID " + id);
            }
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/rest/drinksId/{reservationId}/buy/{code}")
    public ResponseEntity<String> buyDrink(@PathVariable String reservationId, @PathVariable String code) {
        if (checkCode(code)) {
            boolean success = drinksRepository.buyDrink(reservationId);
            if (success) {
                return ResponseEntity.ok("Drink with reservation ID " + reservationId + " purchased successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to purchase drink with reservation ID " + reservationId);
            }
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/rest/drinksId/{id}/checkReservation/{reservationId}/{code}")
    public ResponseEntity<Boolean> checkReservation(@PathVariable String id, @PathVariable String reservationId, @PathVariable String code) {
        if (checkCode(code)) {
            boolean exists = drinksRepository.checkReservation(reservationId, id);
            return ResponseEntity.ok(exists);
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/rest/drinksId/{id}/checkAvailability/{code}")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable String id, @PathVariable String code) {
        if (checkCode(code)) {
            boolean available = drinksRepository.checkAvailability(id);
            return ResponseEntity.ok(available);
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    @PostMapping("/rest/getReserved/{code}")
    public void getReserved(@PathVariable String code) {
        if (checkCode(code)) {
            drinksRepository.getReserved();
        } else {
            throw new CodeNotCorrectException(code);
        }
    }

    private EntityModel<Drink> drinkToEntityModel(String id, Drink drink, String code) {
        return EntityModel.of(drink,
                linkTo(methodOn(DrinksRestController.class).getAllDrinks(code)).withRel("rest/drinks"),
                linkTo(methodOn(DrinksRestController.class).getDrink(id, code)).withRel("rest/drinks/{id}"));
    }
}

