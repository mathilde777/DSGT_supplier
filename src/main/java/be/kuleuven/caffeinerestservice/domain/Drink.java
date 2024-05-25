package be.kuleuven.caffeinerestservice.domain;

import java.util.Objects;

public class Drink {

    protected String id;
    protected String name;
    protected Integer kcal;
    protected Double price;
    protected String description;
    protected Double volume;
    protected Brand brand;

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public Integer getKcal() {return kcal;}

    public void setKcal(Integer kcal) {this.kcal = kcal;}

    public Double getPrice() {return price;}

    public void setPrice(Double price) {this.price = price;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public Double getVolume() {return volume;}

    public void setVolume(Double volume) {this.volume = volume;}

    public Brand getBrand() {return brand;}

    public void setBrand(Brand brand) {this.brand = brand;}

}
