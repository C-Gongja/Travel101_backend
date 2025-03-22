package com.sharavel.sharavel_be.dto;

public class LocationDto {
	private Long id;
	private Integer number;
	private String name;
	private String address;
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Locations{" +
				"name='" + name + '\'' +
				", number=" + number +
				", address=" + address +
				", description=" + description +
				'}';
	}

}
