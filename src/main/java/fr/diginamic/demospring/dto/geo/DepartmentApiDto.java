package fr.diginamic.demospring.dto.geo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Shape of one element returned by https://geo.api.gouv.fr/departements
 */
public record DepartmentApiDto(String code, @JsonProperty("nom") String name, @JsonProperty("codeRegion") String regionCode) {
}