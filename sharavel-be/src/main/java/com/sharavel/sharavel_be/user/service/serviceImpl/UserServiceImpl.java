package com.sharavel.sharavel_be.user.service.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sharavel.sharavel_be.auth.dto.AuthDto;
import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.countries.mapper.CountryMapper;
import com.sharavel.sharavel_be.countries.repository.CountryRepository;
import com.sharavel.sharavel_be.follow.repository.UserFollowRepository;
import com.sharavel.sharavel_be.follow.service.UserFollowService;
import com.sharavel.sharavel_be.security.CustomUserDetails;
import com.sharavel.sharavel_be.security.util.JwtUtil;
import com.sharavel.sharavel_be.socialLink.dto.SocialLinkDto;
import com.sharavel.sharavel_be.socialLink.service.SocialLinkService;
import com.sharavel.sharavel_be.user.dto.UserPersonalInfoDto;
import com.sharavel.sharavel_be.user.dto.UserProfileDto;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;
import com.sharavel.sharavel_be.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private UserFollowRepository userFollowRepository;
	@Autowired
	private UserFollowService userFollowService;
	@Autowired
	private SocialLinkService socialLinksServicer;
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public ResponseEntity<?> verify() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(("User not authenticated"));
		}
		// 인증된 유저 정보
		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Verify User not found"));
		CustomUserDetails userDetails = new CustomUserDetails(user);

		// 새 accessToken 발급
		String newAccessToken = jwtUtil.generateAccessToken(userDetails);

		return ResponseEntity.ok(new AuthDto(
				new AuthDto.UserInfo(userDetails.getUuid(), userDetails.getName(), userDetails.getPicture(),
						userDetails.getAuthorities()),
				newAccessToken));
	}

	@Override
	public UserProfileDto getProfile(String userUuid) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User is not authenticated");
		}
		// 인증된 유저 정보
		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Verify User not found"));

		Users targetUser = userRepository.findByUuid(userUuid)
				.orElseThrow(() -> new RuntimeException("GetProfile User not found"));

		Long followingCount = userFollowService.getFollowingCount(userUuid);
		Long followersCount = userFollowService.getFollowersCount(userUuid);

		List<CountryDto> countries = countryRepository.findDistinctCountriesByUser(targetUser)
				.stream()
				.map(CountryMapper::toDto)
				.toList();

		boolean isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(user.getId(),
				targetUser.getId());
		UserSnippetDto userSnippet = new UserSnippetDto(targetUser.getUuid(), targetUser.getName(),
				targetUser.getUsername(), isFollowing);

		return new UserProfileDto(userSnippet, targetUser, countries, followingCount, followersCount);
	}

	@Override
	public UserPersonalInfoDto getPersonalInfo(String userUuid) {
		Users user = userRepository.findByUuid(userUuid)
				.orElseThrow(() -> new RuntimeException("getPersonalInfo User not found"));

		return new UserPersonalInfoDto(user);
	}

	@Override
	public UserProfileDto updateUserProfile(String userUuid) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateUserProfile'");
	}

	@Override
	public UserPersonalInfoDto updateUserPersonalInfo(String uuid, Map<String, Object> updates) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User is not authenticated");
		}

		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Create Trip User not found"));

		if (updates.containsKey("name")) {
			user.setName((String) updates.get("name"));
		}
		if (updates.containsKey("username")) {
			user.setUsername((String) updates.get("username"));
		}
		if (updates.containsKey("email")) {
			user.setEmail((String) updates.get("email"));
		}
		if (updates.containsKey("country")) {
			user.setCountry((String) updates.get("country"));
		}
		if (updates.containsKey("socialLinks")) {
			Object rawLinks = updates.get("socialLinks");

			if (rawLinks instanceof List<?>) {
				try {
					List<SocialLinkDto> socialLinks = objectMapper.convertValue(
							rawLinks,
							new TypeReference<List<SocialLinkDto>>() {
							});
					socialLinksServicer.setSocialLinks(socialLinks);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException("Failed to parse socialLinks", e);
				}
			} else {
				throw new IllegalArgumentException("Expected socialLinks to be a list");
			}
		}
		if (updates.containsKey("bio")) {
			user.setBio((String) updates.get("bio"));
		}

		userRepository.save(user);
		UserPersonalInfoDto updatedUserInfo = new UserPersonalInfoDto(user);
		return updatedUserInfo;
	}
}
