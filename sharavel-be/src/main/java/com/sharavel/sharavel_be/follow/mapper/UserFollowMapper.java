package com.sharavel.sharavel_be.follow.mapper;

import com.sharavel.sharavel_be.follow.entity.UserFollow;
import com.sharavel.sharavel_be.follow.repository.UserFollowRepository;
import com.sharavel.sharavel_be.user.dto.UserSnippetDto;
import com.sharavel.sharavel_be.user.entity.Users;

public class UserFollowMapper {
	public static UserSnippetDto toFollowerDto(UserFollow userFollow, Users currentUser,
			UserFollowRepository userFollowRepository) {
		Users followerUser = userFollow.getFollower();
		boolean isFollowing = false;

		System.out.println("!!!!!!currentUser" + currentUser);
		if (currentUser != null) {
			isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), followerUser.getId());
		}

		return new UserSnippetDto(
				followerUser.getUuid(),
				followerUser.getName(),
				followerUser.getUsername(),
				isFollowing);
	}

	public static UserSnippetDto toFollowingDto(UserFollow userFollow, Users currentUser,
			UserFollowRepository userFollowRepository) {
		Users followingUser = userFollow.getFollowing();
		boolean isFollowing = false;

		if (currentUser != null) {
			isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), followingUser.getId());
		}

		return new UserSnippetDto(
				followingUser.getUuid(),
				followingUser.getName(),
				followingUser.getUsername(),
				isFollowing);
	}
}
