package fr.diginamic.demospring.dto;

import fr.diginamic.demospring.model.City;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object exchanged with API clients for a {@link City}.
 *
 * <p>The shape is adapted to the front end rather than to the persistence model:
 * the owning department is referenced either by its id ({@link #departmentId}) or
 * by its code ({@link #departmentCode}), never as a nested object. At least one of
 * the two must be provided when creating or updating a city.</p>
 */
@Schema(description = "A city, as exchanged with API clients.")
public class CityDto {

    @Schema(description = "Primary key. Ignored on creation, echoed back on reads.", example = "1")
    private Integer id;

    @Schema(description = "City name.", example = "Lyon", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "City name cannot be null.")
    @Size(min = 2, message = "City name must contain at least 2 letters.")
    private String name;

    @Schema(description = "Number of inhabitants.", example = "520000")
    @Min(value = 1, message = "City population must be at least 1 inhabitant.")
    private int population;

    @Schema(description = "Id of the owning department. Provide this or 'departmentCode'.", example = "69")
    private Integer departmentId;

    @Schema(description = "Code of the owning department. Provide this or 'departmentId'. "
            + "An unknown code triggers creation of the department.", example = "69")
    private String departmentCode;

    /** Default constructor required by Jackson. */
    public CityDto() {
    }

    /**
     * Builds a DTO from a persistent entity.
     *
     * @param city the entity to convert, never {@code null}
     * @return the matching DTO, with department id and code populated when the
     *         city has a department
     */
    public static CityDto fromEntity(City city) {
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setPopulation(city.getPopulation());

        if (city.getDepartment() != null) {
            dto.setDepartmentId(city.getDepartment().getId());
            dto.setDepartmentCode(city.getDepartment().getCode());
        }

        return dto;
    }

    /**
     * Builds a bare entity from this DTO.
     *
     * <p>The department is <em>not</em> resolved here; callers must attach it via
     * {@link City#setDepartment}.</p>
     *
     * @return a new {@link City} carrying the id, name and population of this DTO
     */
    public City toEntity() {
        City city = new City();
        city.setId(this.id);
        city.setName(this.name);
        city.setPopulation(this.population);
        return city;
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

    /** @return the owning department code, or {@code null} */
    public String getDepartmentCode() {
        return departmentCode;
    }

    /** @param departmentCode the owning department code to set */
    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    /** @return the owning department id, or {@code null} */
    public Integer getDepartmentId() {
        return departmentId;
    }

    /** @param departmentId the owning department id to set */
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
}
