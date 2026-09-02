package fr.diginamic.demospring.model;

import jakarta.persistence.*;

/**
 * JPA entity representing a city.
 *
 * <p>Each city belongs to exactly one {@link Department} (many cities per
 * department). This class is the persistence model only; data exchanged with API
 * clients goes through {@link fr.diginamic.demospring.dto.CityDto}.</p>
 */
@Entity
public class City {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** City name (e.g. {@code "Lyon"}). */
    private String name;

    /** Number of inhabitants. */
    private int population;

    /** Department this city belongs to. */
    @ManyToOne
    private Department department;

    /** Default constructor required by JPA. */
    public City() {
    }

    /**
     * Convenience constructor.
     *
     * @param id         primary key, may be {@code null} for a new city
     * @param name       city name
     * @param population number of inhabitants
     */
    public City(Integer id, String name, int population) {
        this.id = id;
        this.name = name;
        this.population = population;
    }

    /** @return the primary key */
    public Integer getId() {
        return id;
    }

    /** @param id the primary key to set */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return the city name */
    public String getName() {
        return name;
    }

    /** @param name the city name to set */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the number of inhabitants */
    public int getPopulation() {
        return population;
    }

    /** @param population the number of inhabitants to set */
    public void setPopulation(int population) {
        this.population = population;
    }

    /** @return the owning department */
    public Department getDepartment() {
        return department;
    }

    /** @param department the owning department to set */
    public void setDepartment(Department department) {
        this.department = department;
    }
}
