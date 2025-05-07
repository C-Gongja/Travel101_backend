package com.sharavel.sharavel_be.countries.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.countries.entity.Country;
import com.sharavel.sharavel_be.countries.mapper.CountryMapper;
import com.sharavel.sharavel_be.countries.repository.CountryRepository;

@RestController
@RequestMapping("/public/country")
public class CountryController {
	@Autowired
	private CountryRepository countryRepository;

	@GetMapping("/all")
	public ResponseEntity<List<CountryDto>> getAllCountry() {
		List<Country> allCountries = countryRepository.findAll();
		List<CountryDto> countryDtos = allCountries.stream()
				.map(CountryMapper::toDto)
				.collect(Collectors.toList());

		if (countryDtos == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.ok(countryDtos);
	}
}
