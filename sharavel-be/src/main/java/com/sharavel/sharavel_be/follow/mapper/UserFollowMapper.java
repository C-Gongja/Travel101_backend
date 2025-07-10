package com.sharavel.sharavel_be.follow.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sharavel.sharavel_be.follow.entity.UserFollow;
import com.sharavel.sharavel_be.follow.repository.UserFollowRepository;
import com.sharavel.sharavel_be.s3bucket.service.S3ProfileService;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;
import com.sharavel.sharavel_be.user.entity.Users;

@Component
public class UserFollowMapper {
	@Autowired
	private S3ProfileService s3ProfileService;
	@Autowired
	private UserFollowRepository userFollowRepository;

	public UserSnippetDto toFollowerDto(UserFollow userFollow, Users currentUser) {
		Users followerUser = userFollow.getFollower();
		boolean isFollowing = false;

		String picture = followerUser.getPicture();
		if (picture != null && picture.startsWith("sharavel-profile:")) {
			String s3Key = picture.replace("sharavel-profile:", "");
			picture = s3ProfileService.generatePresignedUrl(s3Key, 604800).toString();
		}

		if (currentUser != null) {
			isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), followerUser.getId());
		}

		return new UserSnippetDto(
				followerUser.getUuid(),
				picture,
				followerUser.getName(),
				followerUser.getUsername(),
				isFollowing);
	}

	public UserSnippetDto toFollowingDto(UserFollow userFollow, Users currentUser) {
		Users followingUser = userFollow.getFollowing();
		boolean isFollowing = false;

		String picture = followingUser.getPicture();
		if (picture != null && picture.startsWith("sharavel-profile:")) {
			String s3Key = picture.replace("sharavel-profile:", "");
			picture = s3ProfileService.generatePresignedUrl(s3Key, 604800).toString();
		}

		if (currentUser != null) {
			isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), followingUser.getId());
		}

		return new UserSnippetDto(
				followingUser.getUuid(),
				picture,
				followingUser.getName(),
				followingUser.getUsername(),
				isFollowing);
	}
}
