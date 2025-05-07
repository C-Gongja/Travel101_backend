package com.sharavel.sharavel_be.user.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.socialLink.dto.SocialLinkDto;
import com.sharavel.sharavel_be.socialLink.mapper.SocialLinkMapper;
import com.sharavel.sharavel_be.user.entity.Users;

public class UserProfileDto {
	private UserSnippetDto userSnippet;
	private List<SocialLinkDto> socialLinks;
	private List<CountryDto> countries;
	private Long followingCount;
	private Long followersCount;
	private Integer totalTrips;
	private Integer totalTravelDays;
	private String bio;

	public UserProfileDto(UserSnippetDto userSnippet, Users user, List<CountryDto> countries, Long followingCount,
			Long followersCount) {
		this.userSnippet = userSnippet;
		this.socialLinks = user.getSocialLinks()
				.stream()
				.map(SocialLinkMapper::toDto)
				.collect(Collectors.toList());
		this.totalTrips = user.getTotalTrips();
		this.totalTravelDays = user.getTotalTripDays();
		this.bio = user.getBio();
		this.countries = countries;
		this.followingCount = followingCount;
		this.followersCount = followersCount;
	}

	public UserSnippetDto getUserSnippet() {
		return userSnippet;
	}

	public void setUserSnippet(UserSnippetDto userSnippet) {
		this.userSnippet = userSnippet;
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

	public Long getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(Long folloingCount) {
		this.followingCount = folloingCount;
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
