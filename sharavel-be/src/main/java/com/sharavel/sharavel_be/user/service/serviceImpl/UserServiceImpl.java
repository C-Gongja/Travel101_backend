package com.sharavel.sharavel_be.user.service.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.sharavel.sharavel_be.s3bucket.service.S3ProfileService;
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
	private S3ProfileService s3ProfileService;
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

		String picture = user.getPicture();
		if (picture != null && picture.startsWith("sharavel-profile:")) {
			String s3Key = picture.replace("sharavel-profile:", "");
			picture = s3ProfileService.generatePresignedUrl(s3Key, 604800).toString();
		}

		return ResponseEntity.ok(new AuthDto(
				new AuthDto.UserInfo(userDetails.getUuid(), userDetails.getName(), user.getUsername(),
						picture, userDetails.getAuthorities()),
				newAccessToken));
	}

	@Override
	public UserProfileDto getProfile(String userUuid) {
		boolean isFollowing = false;
		Users targetUser = userRepository.findByUuid(userUuid)
				.orElseThrow(() -> new RuntimeException("GetProfile User not found"));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			String email = authentication.getName();
			Users currentUser = userRepository.findByEmail(email)
					.orElse(null);

			isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), targetUser.getId());
		}

		Long followingCount = userFollowService.getFollowingCount(userUuid);
		Long followersCount = userFollowService.getFollowersCount(userUuid);

		List<CountryDto> countries = countryRepository.findDistinctCountriesByUser(targetUser)
				.stream()
				.map(CountryMapper::toDto)
				.toList();

		String picture = targetUser.getPicture();
		if (picture != null && picture.startsWith("sharavel-profile:")) {
			String s3Key = picture.replace("sharavel-profile:", "");
			picture = s3ProfileService.generatePresignedUrl(s3Key, 604800).toString();
		}

		// 생성 후에는 unreferenced media가 없을 것이므로 deleteUnreferencedMedia 호출은 보통 생략
		UserSnippetDto userSnippet = new UserSnippetDto(targetUser.getUuid(), picture, targetUser.getName(),
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
	public UserPersonalInfoDto updateUserPersonalInfo(String uuid, Map<String, Object> updates) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User is not authenticated");
		}

		String email = authentication.getName();
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Create Trip User not found"));
		String userUuid = user.getUuid();

		if (!uuid.equals(userUuid)) {
			throw new IllegalStateException("Invalid user request");
		}

		updates.forEach((key, value) -> {
			switch (key) {
				case "name" -> user.updateName((String) value, userUuid);
				case "username" -> {
					String newUsername = (String) value;
					if (checkUniqueUsername(newUsername)) {
						user.updateUsername(newUsername, userUuid, userRepository);
					} else {
						throw new IllegalArgumentException("'" + newUsername + "' already exists.");
					}
				}
				case "email" -> user.updateEmail((String) value, userUuid, userRepository);
				case "country" -> user.updateCountry((String) value, userUuid);
				case "socialLinks" -> {
					if (value instanceof List<?>) {
						try {
							List<SocialLinkDto> socialLinks = objectMapper.convertValue(
									value,
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
				case "bio" -> user.updateBio((String) value, userUuid);
				default -> {
					throw new IllegalArgumentException("Unknown field: " + key);
				}
			}
		});

		userRepository.save(user);
		UserPersonalInfoDto updatedUserInfo = new UserPersonalInfoDto(user);
		return updatedUserInfo;
	}

	@Override
	public UserProfileDto updateUserProfile(String userUuid) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'updateUserProfile'");
	}

	@Override
	public String generateRandomUsername(String name) {
		String chars = "0123456789";
		StringBuilder sb = new StringBuilder(name);
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		String username = sb.toString();
		while (userRepository.existsByUsername(username)) {
			username = generateRandomUsername(name);
		}
		return username;
	}

	@Override
	public boolean checkUniqueUsername(String username) {
		return !userRepository.existsByUsername(username);
	}
}
