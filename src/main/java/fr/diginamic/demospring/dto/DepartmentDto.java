package fr.diginamic.demospring.dto;

import fr.diginamic.demospring.model.Department;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object exchanged with API clients for a {@link Department}.
 *
 * <p>Flat view of the entity ({@code id}, {@code code}, {@code name}); the list of
 * cities is intentionally omitted.</p>
 */
@Schema(description = "A French department, as exchanged with API clients.")
public class DepartmentDto {

    @Schema(description = "Primary key. Ignored on creation, echoed back on reads.", example = "1")
    private Integer id;

    @Schema(description = "INSEE department code.", example = "69", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Department code cannot be blank.")
    private String code;

    @Schema(description = "Department name.", example = "Rhône")
    private String name;

    /** Default constructor required by Jackson. */
    public DepartmentDto() {
    }

    /**
     * Builds a DTO from a persistent entity.
     *
     * @param department the entity to convert, never {@code null}
     * @return the matching DTO
     */
    public static DepartmentDto fromEntity(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setCode(department.getCode());
        dto.setName(department.getName());
        return dto;
    }

    /**
     * Builds an entity from this DTO.
     *
     * <p>The {@code id} is intentionally not copied: this method is used on
     * creation only, and the key is assigned by {@code @GeneratedValue}.</p>
     *
     * @return a new {@link Department} carrying the code and name of this DTO
     */
    public Department toEntity() {
        Department department = new Department();
        department.setCode(this.code);
        department.setName(this.name);
        return department;
    }

    /** @return the primary key */
    public Integer getId() {
        return id;
    }

    /** @param id the primary key to set */
    public void setId(Integer id) {
        this.id = id;
    }

    /** @return the INSEE department code */
    public String getCode() {
        return code;
    }

    /** @param code the INSEE department code to set */
    public void setCode(String code) {
        this.code = code;
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
