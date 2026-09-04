package fr.diginamic.demospring.dto.geo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Shape of one element returned by https://geo.api.gouv.fr/regions
 */
public record RegionApiDto(String code, @JsonProperty("nom") String name) {
}