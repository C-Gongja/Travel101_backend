package com.sharavel.sharavel_be.countries.mapper;

import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.countries.entity.Country;

public class CountryMapper {
	public static CountryDto toDto(Country country) {
		return new CountryDto(country);
	}

	public static Country toEntity(CountryDto dto) {
		Country country = new Country();
		country.setIso2(dto.getIso2());
		country.setName(dto.getIso2());
		country.setFlag(dto.getFlag());
		return country;
	}
}
