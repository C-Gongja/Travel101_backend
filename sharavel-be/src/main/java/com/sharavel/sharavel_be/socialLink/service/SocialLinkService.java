package com.sharavel.sharavel_be.socialLink.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sharavel.sharavel_be.socialLink.dto.SocialLinkDto;

public interface SocialLinkService {
	ResponseEntity<?> setSocialLinks(List<SocialLinkDto> socialLinkDtoList);

	List<SocialLinkDto> getSocialLinks(String uuid);
}
