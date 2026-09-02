package fr.diginamic.demospring.dto;

import fr.diginamic.demospring.model.City;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CityDto {

    private Integer id;

    @NotNull(message = "City name cannot be null.")
    @Size(min = 2, message = "City name must contain at least 2 letters.")
    private String name;

    @Min(value = 1, message = "City population must be at least 1 inhabitant.")
    private int population;

    private Integer departmentId;

    private String departmentCode;

    public CityDto() {
    }

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

    public City toEntity() {
        City city = new City();
        city.setId(this.id);
        city.setName(this.name);
        city.setPopulation(this.population);
        return city;
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

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
}
