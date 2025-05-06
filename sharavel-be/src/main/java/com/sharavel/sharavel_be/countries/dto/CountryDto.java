package com.sharavel.sharavel_be.countries.dto;

import com.sharavel.sharavel_be.countries.entity.Country;

public class CountryDto {
	private String iso2;
	private String name;
	private String flag;

	public CountryDto(Country country) {
		this.iso2 = country.getIso2();
		this.name = country.getName();
		this.flag = country.getFlag();
	}

	public String getIso2() {
		return iso2;
	}

	public void setIso2(String iso2) {
		this.iso2 = iso2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
