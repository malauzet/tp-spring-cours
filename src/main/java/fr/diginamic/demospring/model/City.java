package fr.diginamic.demospring.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class City {

    private Integer id;

    @NotNull(message = "City name cannot be null.")
    @Size(min = 2, message = "City name must contain at least 2 letters.")
    private String name;

    @Min(value = 1, message = "City population must be at least 1 inhabitant.")
    private int population;

    public City() {
    }

    public City(Integer id, String name, int population) {
        this.id = id;
        this.name = name;
        this.population = population;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}
