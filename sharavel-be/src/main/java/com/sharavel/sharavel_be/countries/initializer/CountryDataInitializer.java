package com.sharavel.sharavel_be.countries.initializer;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharavel.sharavel_be.countries.entity.Country;
import com.sharavel.sharavel_be.countries.repository.CountryRepository;

import jakarta.annotation.PostConstruct;

@Component
public class CountryDataInitializer {
	@Autowired
	private final CountryRepository countryRepository; // JPA Repository

	public CountryDataInitializer(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}

	@PostConstruct
	public void init() {
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/countries.json");
			if (inputStream != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				List<Country> countries = objectMapper.readValue(inputStream, new TypeReference<List<Country>>() {
				});
				countryRepository.saveAll(countries);
			} else {
				System.err.println("countries.json 파일을 찾을 수 없습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
