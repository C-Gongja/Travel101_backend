package com.sharavel.sharavel_be.socialLink.mapper;

import com.sharavel.sharavel_be.socialLink.dto.SocialLinkDto;
import com.sharavel.sharavel_be.socialLink.entity.SocialLink;
import com.sharavel.sharavel_be.user.entity.Users;

public class SocialLinkMapper {
	public static SocialLinkDto toDto(SocialLink socialLink) {
		return new SocialLinkDto(
				socialLink.getPlatform(),
				socialLink.getUrl());
	}

	public static SocialLink toEntity(SocialLinkDto dto, Users user) {
		SocialLink socialLink = new SocialLink();
		socialLink.setPlatform(dto.getPlatform());
		socialLink.setUrl(dto.getUrl());
		socialLink.setUser(user);
		return socialLink;
	}
}
