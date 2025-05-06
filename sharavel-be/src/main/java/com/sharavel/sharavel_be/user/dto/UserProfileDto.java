package com.sharavel.sharavel_be.user.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.socialLink.dto.SocialLinkDto;
import com.sharavel.sharavel_be.socialLink.mapper.SocialLinkMapper;
import com.sharavel.sharavel_be.user.entity.Users;

public class UserProfileDto {
	private String uuid;
	private String name;
	private String username;
	private List<SocialLinkDto> socialLinks;
	private List<CountryDto> countries;
	private Long folloingCount;
	private Long followersCount;
	private Integer totalTrips;
	private Integer totalTravelDays;
	private String bio;

	public UserProfileDto(Users user, List<CountryDto> countries, Long followingCount, Long followersCount) {
		this.uuid = user.getUuid();
		this.name = user.getName();
		this.username = user.getUsername();
		this.socialLinks = user.getSocialLinks()
				.stream()
				.map(SocialLinkMapper::toDto)
				.collect(Collectors.toList());
		this.countries = countries;
		this.folloingCount = followingCount;
		this.followersCount = followersCount;
		this.totalTrips = user.getTotalTrips();
		this.totalTravelDays = user.getTotalTripDays();
		this.bio = user.getBio();
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

	public List<SocialLinkDto> getSocialLinks() {
		return socialLinks;
	}

	public void setSocialLinks(List<SocialLinkDto> socialLinks) {
		this.socialLinks = socialLinks;
	}

	public List<CountryDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDto> countries) {
		this.countries = countries;
	}

	public Long getFolloingCount() {
		return folloingCount;
	}

	public void setFolloingCount(Long folloingCount) {
		this.folloingCount = folloingCount;
	}

	public Long getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(Long followersCount) {
		this.followersCount = followersCount;
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
