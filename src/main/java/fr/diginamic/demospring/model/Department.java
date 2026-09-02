package fr.diginamic.demospring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

/**
 * JPA entity representing a French department.
 *
 * <p>A department owns zero or more {@link City} instances. The {@code cities}
 * collection is marked {@link JsonIgnore} to break the bidirectional
 * serialization cycle; API responses use
 * {@link fr.diginamic.demospring.dto.DepartmentDto} instead.</p>
 */
@Entity
public class Department {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** INSEE department code (e.g. {@code "69"}, {@code "2A"}). */
    private String code;

    /** Department name (e.g. {@code "Rhône"}). */
    private String name;

    /** Cities belonging to this department; not serialized. */
    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<City> cities;

    /** Default constructor required by JPA. */
    public Department() {
    }

    /**
     * Convenience constructor.
     *
     * @param id   primary key, may be {@code null} for a new department
     * @param code INSEE department code
     * @param name department name
     */
    public Department(Integer id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    /** @return the INSEE department code */
    public String getCode() {
        return code;
    }

    /** @param code the INSEE department code to set */
    public void setCode(String code) {
        this.code = code;
    }

    /** @return the primary key */
    public Integer getId() {
        return id;
    }

    /** @param id the primary key to set */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return the department name */
    public String getName() {
        return name;
    }

    /** @param name the department name to set */
    public void setName(String name) {
        this.name = name;
    }
}
