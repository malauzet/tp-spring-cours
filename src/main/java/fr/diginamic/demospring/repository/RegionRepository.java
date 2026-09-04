package fr.diginamic.demospring.repository;

import fr.diginamic.demospring.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {

    Optional<Region> findByCodeIgnoreCase(String code);
}