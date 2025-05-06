package com.sharavel.sharavel_be.socialLink.service.serviceImple;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.socialLink.dto.SocialLinkDto;
import com.sharavel.sharavel_be.socialLink.entity.SocialLink;
import com.sharavel.sharavel_be.socialLink.mapper.SocialLinkMapper;
import com.sharavel.sharavel_be.socialLink.repository.SocialLinkRepository;
import com.sharavel.sharavel_be.socialLink.service.SocialLinkService;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class SocialLinkServiceImpl implements SocialLinkService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SocialLinkRepository socialLinkRepository;

	// SocialLink 객체 생성 메서드
	private SocialLink buildSocialLink(SocialLinkDto socialLinkDto, Users user) {
		SocialLink socialLink = new SocialLink();
		socialLink.setUser(user);
		socialLink.setPlatform(socialLinkDto.getPlatform());
		socialLink.setUrl(socialLinkDto.getUrl());
		return socialLink;
	}

	@Override
	@Transactional
	public ResponseEntity<?> setSocialLinks(List<SocialLinkDto> socialLinkDtoList) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("User is not authenticated");
		}

		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("User not found"));

		// 기존 링크 삭제
		socialLinkRepository.deleteByUser(user);

		// 새로 저장
		for (SocialLinkDto dto : socialLinkDtoList) {
			SocialLink link = buildSocialLink(dto, user);
			socialLinkRepository.save(link);
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Successfully updated social links");
	}

	@Override
	public List<SocialLinkDto> getSocialLinks(String uuid) {
		Users user = userRepository.findByUuid(uuid)
				.orElseThrow(() -> new IllegalStateException("getSocialLinks User not found"));

		List<SocialLink> socialLinks = socialLinkRepository.findByUser(user);

		return socialLinks.stream()
				.map(SocialLinkMapper::toDto)
				.collect(Collectors.toList());
	}
}
