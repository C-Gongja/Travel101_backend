package com.sharavel.sharavel_be.dto;

import com.sharavel.sharavel_be.entity.Users;

public class UserProfileDto {
	private String id;
	private String name;
	private String username;
	private String email;
	private String country;
	private String state;
	private String city;

	public UserProfileDto(Users user) {
		this.id = user.getUuid();
		this.name = user.getName();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.country = user.getCountry();
		this.state = user.getState();
		this.city = user.getCity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
