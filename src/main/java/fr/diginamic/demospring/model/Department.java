package fr.diginamic.demospring.model;

import jakarta.persistence.*;

import java.util.List;

/**
 * JPA entity representing a French department.
 *
 * <p>A department owns zero or more {@link City} instances. This class is the
 * persistence model only; data exchanged with API clients goes through
 * {@link fr.diginamic.demospring.dto.DepartmentDto}.</p>
 */
@Entity
@Table(name = "departement")
public class Department {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** INSEE department code (e.g. {@code "69"}, {@code "2A"}). */
    @Column(name = "code")
    private String code;

    /** Department name (e.g. {@code "Rhône"}). */
    @Column(name = "nom")
    private String name;

    /** Cities belonging to this department. */
    @OneToMany(mappedBy = "department")
    private List<City> cities;

    /** Default constructor required by JPA. */
    public Department() {
    }

    /**
     * Convenience constructor for building a new (unsaved) department; the id is
     * left for {@code @GeneratedValue} to assign.
     *
     * @param code INSEE department code
     * @param name department name
     */
    public Department(String code, String name) {
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
