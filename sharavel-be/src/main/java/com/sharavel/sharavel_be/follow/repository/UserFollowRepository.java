package com.sharavel.sharavel_be.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharavel.sharavel_be.follow.entity.UserFollow;
import com.sharavel.sharavel_be.user.entity.Users;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
	// 특정 유저가 팔로우하는 다른 유저 조회
	Optional<UserFollow> findByFollowerAndFollowing(Users follower, Users following);

	// 특정 유저가 팔로우한 모든 유저 조회
	List<UserFollow> findByFollower(Users follower);

	// 특정 유저를 팔로우한 모든 유저 조회
	List<UserFollow> findByFollowing(Users following);

	// 팔로우를 제거 (언팔로우)
	void deleteByFollowerAndFollowing(Users follower, Users following);
}
