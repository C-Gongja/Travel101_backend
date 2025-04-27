package com.sharavel.sharavel_be.user.dto;

public class UserAccountDto {
	private String uuid;
	private String name;
	private String username;
	private String social_accounts;
	private String countries;
	private String folloing;
	private String followers;
	private Integer totalTrips;
	private Integer totalTravelDays;
	private String bio;

	public UserAccountDto(String uuid, String name, String username, String social_accounts, String countries,
			String folloing, String followers, Integer totalTrips, Integer totalTravelDays, String bio) {
		this.uuid = uuid;
		this.name = name;
		this.username = username;
		this.social_accounts = social_accounts;
		this.countries = countries;
		this.folloing = folloing;
		this.followers = followers;
		this.totalTrips = totalTrips;
		this.totalTravelDays = totalTravelDays;
		this.bio = bio;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getSocial_accounts() {
		return social_accounts;
	}

	public void setSocial_accounts(String social_accounts) {
		this.social_accounts = social_accounts;
	}

	public String getCountries() {
		return countries;
	}

	public void setCountries(String countries) {
		this.countries = countries;
	}

	public String getFolloing() {
		return folloing;
	}

	public void setFolloing(String folloing) {
		this.folloing = folloing;
	}

	public String getFollowers() {
		return followers;
	}

	public void setFollowers(String followers) {
		this.followers = followers;
	}

	public Integer getTotalTrips() {
		return totalTrips;
	}

	public void setTotalTrips(Integer totalTrips) {
		this.totalTrips = totalTrips;
	}

	public Integer getTotalTravelDays() {
		return totalTravelDays;
	}

	public void setTotalTravelDays(Integer totalTravelDays) {
		this.totalTravelDays = totalTravelDays;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

}
