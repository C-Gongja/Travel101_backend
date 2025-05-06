package com.sharavel.sharavel_be.user.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sharavel.sharavel_be.socialLink.dto.SocialLinkDto;
import com.sharavel.sharavel_be.socialLink.mapper.SocialLinkMapper;
import com.sharavel.sharavel_be.user.entity.Users;

public class UserPersonalInfoDto {
	private String uuid;
	private String picture;
	private String name;
	private String username;
	private String email;
	private List<SocialLinkDto> socialLinks;
	private String country;
	private String bio;

	public UserPersonalInfoDto(Users user) {
		this.uuid = user.getUuid();
		this.name = user.getName();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.picture = user.getPicture();
		this.socialLinks = user.getSocialLinks()
				.stream()
				.map(SocialLinkMapper::toDto)
				.collect(Collectors.toList());
		this.country = user.getCountry();
		this.bio = user.getBio();
	}

	public String getId() {
		return uuid;
	}

	public void setId(String uuid) {
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

	public List<SocialLinkDto> getSocialLinks() {
		return socialLinks;
	}

	public void setSocialLinks(List<SocialLinkDto> socialLinks) {
		this.socialLinks = socialLinks;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
}
