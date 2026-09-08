package fr.diginamic.demospring.service;

import fr.diginamic.demospring.dto.CityDto;
import fr.diginamic.demospring.exception.FunctionalException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.model.Department;
import fr.diginamic.demospring.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private CityService cityService;

    // ---- getCityById ----

    @Test
    void getCityById_shouldReturnCity_whenFound() {
        City city = new City();
        city.setId(1);
        city.setName("Lyon");
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));

        Optional<CityDto> result = cityService.getCityById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Lyon");
    }

    @Test
    void getCityById_shouldReturnEmpty_whenNotFound() {
        when(cityRepository.findById(99)).thenReturn(Optional.empty());

        Optional<CityDto> result = cityService.getCityById(99);

        assertThat(result).isEmpty();
    }

    // ---- addCity ----

    @Test
    void addCity_shouldThrowFunctionalException_whenCityAlreadyExistsInDepartment() throws FunctionalException {
        CityDto dto = new CityDto();
        dto.setName("Lyon");
        dto.setDepartmentId(69);

        Department department = new Department();
        department.setId(69);

        when(departmentService.resolve(69, null)).thenReturn(department);
        when(cityRepository.existsByNameIgnoreCaseAndDepartmentId("Lyon", 69)).thenReturn(true);

        assertThatThrownBy(() -> cityService.addCity(dto))
                .isInstanceOf(FunctionalException.class)
                .hasMessageContaining("already exists");

        // on vérifie qu'on ne sauvegarde jamais dans ce cas
        verify(cityRepository, never()).save(any());
    }

    @Test
    void addCity_shouldSaveCity_whenNoConflict() throws FunctionalException {
        CityDto dto = new CityDto();
        dto.setName("Lyon");
        dto.setPopulation(500000);
        dto.setDepartmentId(69);

        Department department = new Department();
        department.setId(69);

        City savedEntity = new City();
        savedEntity.setId(1);
        savedEntity.setName("Lyon");
        savedEntity.setDepartment(department);

        when(departmentService.resolve(69, null)).thenReturn(department);
        when(cityRepository.existsByNameIgnoreCaseAndDepartmentId("Lyon", 69)).thenReturn(false);
        when(cityRepository.save(any(City.class))).thenReturn(savedEntity);

        CityDto result = cityService.addCity(dto);

        assertThat(result.getName()).isEqualTo("Lyon");
        verify(cityRepository).save(any(City.class));
    }

    // ---- deleteCity ----

    @Test
    void deleteCity_shouldThrowNotFoundException_whenCityDoesNotExist() {
        when(cityRepository.existsById(42)).thenReturn(false);

        assertThatThrownBy(() -> cityService.deleteCity(42))
                .isInstanceOf(NotFoundException.class);

        verify(cityRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteCity_shouldDelete_whenCityExists() throws NotFoundException {
        when(cityRepository.existsById(1)).thenReturn(true);

        cityService.deleteCity(1);

        verify(cityRepository).deleteById(1);
    }
}